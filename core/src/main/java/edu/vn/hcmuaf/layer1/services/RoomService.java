package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.dao.UserDAO;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.RoomRedisClusterHelper;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.channel.RoomNotify;
import edu.vn.hcmuaf.layer2.redis.context.RoomContext;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import jakarta.websocket.Session;

import java.util.List;
import java.util.Set;

public class RoomService {
    public static final RoomService instance = new RoomService();
    public static final RoomNotify roomNotify = RoomNotify.me();
    public static final RoomRedisClusterHelper roomRedisClusterHelper = RoomRedisClusterHelper.me();
    public static final SessionCache sessionCache = SessionCache.me();


    public static RoomService me() {
        return instance;
    }

    private RoomService() {
    }

    public void createRoom(Proto.ReqCreateRoom packet) {
        //gen id 6 so  int
        String roomId = genIdRoom();
        // tao room
        createRoom(roomId, packet.getUserId(), packet.getTestId());
    }

    private String genIdRoom() {
        String id = String.valueOf(System.currentTimeMillis()).substring(7);
        return roomRedisClusterHelper.containKey(id) ? genIdRoom() : id;
    }

    public void createRoom(String roomId, String hostId, String testId) {
        // tao room context
        RoomContext room = RoomContext.builder().roomId(roomId).hostId(hostId).testId(testId).build();
        // add room va room context vao redis
        roomRedisClusterHelper.addRoomAndRoomContext(roomId, hostId, room);
        // dang ki xuong redis 1 channel, lang nghe channel do
        roomNotify.subscribe(roomId);
        System.out.println("Nguoi choi : " + hostId + " -- da tao phong : " + roomId);
    }

    public void joinRoom(String roomId, String sessionId) {
        // lay ra session context
        SessionContext sessionContext = sessionCache.get(sessionId);
        // set room id
        sessionContext.setRoomId(roomId);

        sessionCache.add(sessionId, sessionContext);// add local cache
        sessionCache.update(sessionContext);// update redis


        // lay ra toan bo session dang online trong server nay
        Set<String> sessionListInServer = SessionCache.me().getKeys();
        // them session vao room
        roomRedisClusterHelper.addUsersToRoom(roomId, sessionId);

        // tao goi tin resJoinRoom
        Proto.ResJoinRoom resJoinRoom = Proto.ResJoinRoom.newBuilder().setName(sessionContext.getUser().getUsername()).setSessionId(sessionId).build();
//        Proto.ResJoinRoom resJoinRoom = Proto.ResJoinRoom.newBuilder().setName("hai").setSessionId(sessionId).build();

        Proto.Packet packet = Proto.Packet.newBuilder().setResJoinRoom(resJoinRoom).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // thong bao cho tat ca moi nguoi trong phong
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);
        sessionList.forEach(s -> {

            if (sessionListInServer.contains(s)) {
                Session session = SessionManage.me().get(s);
                session.getAsyncRemote().sendText("Co " + sessionId + " moi vao phong");
                // gui goi tin toi session
                session.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });
        System.out.println("session : " + sessionId + " da vao phong : " + roomId);
    }

    public void outRoom(String roomId, String sessionId) {
        // lay ra session context
        SessionContext sessionContext = sessionCache.get(sessionId);
        // set room id
        sessionContext.setRoomId(null);

        sessionCache.add(sessionId, sessionContext);// add local cache
        sessionCache.update(sessionContext);// update redis

        // lay ra toan bo session dang online trong server nay
        Set<String> sessionListInServer = SessionCache.me().getKeys();
        // xoa session khoi room
        roomRedisClusterHelper.deleteUserFromRoom(roomId, sessionId);

        // tao goi tin resOutRoom
        Proto.ResOutRoom resOutRoom = Proto.ResOutRoom.newBuilder().setName(sessionContext.getUser().getUsername()).setSessionId(sessionId).build();
        Proto.Packet packet = Proto.Packet.newBuilder().setResOutRoom(resOutRoom).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // thong bao cho tat ca moi nguoi trong phong
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);
        sessionList.forEach(s -> {

            if (sessionListInServer.contains(s)) {
                Session session = SessionManage.me().get(s);
                session.getAsyncRemote().sendText("Co " + sessionId + " thoat phong");
                // gui goi tin toi session
                session.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });

        System.out.println("session : " + sessionId + " da thoat khoi phong : " + roomId);
    }

    public void closeRoom(String token, String roomId) {
//        // lay ra room context
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(roomId);
//        // kiem tra token
        if (!UserDAO.checkToken(roomContext.getHostId(), token)) {
            System.out.println("khong the xoa phong vi k phai chu phong");
            return;
        }
//       //xoa roomlistner
        roomNotify.unsubscribe(roomId);

//
//        // lay ra ss trong phong
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);
//        // lay ra ss trong server
        Set<String> sessionListInServer = SessionCache.me().getKeys();
//        // tao goi tin resCloseRoom
        Proto.ResCloseRoom resCloseRoom = Proto.ResCloseRoom.newBuilder().setStatus(1).build();
        Proto.Packet packet = Proto.Packet.newBuilder().setResCloseRoom(resCloseRoom).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
//        // thong bao cho tat ca moi nguoi trong phong
        sessionList.forEach(s -> {
            if (sessionListInServer.contains(s)) {
                Session session = SessionManage.me().get(s);
                session.getAsyncRemote().sendText("Phong da bi dong");
                // gui goi tin toi session
                session.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });
//      xoa room va room context trong redis
        roomRedisClusterHelper.removeRoom(roomId, roomContext.getHostId());
//      Thong bao voi cac server khac ve thong tin phong co roomid da bi dong
//        Proto.PacketWrapper reqCloseRoomPakageWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setReqCloseRoom(Proto.ReqCloseRoom.newBuilder().setRoomId(roomId).build()).build()).build();
//        MainChannelNotify.me().publish(reqCloseRoomPakageWrapper);
//
        System.out.println("phong : " + roomId + " da bi xoa");

    }

    public static void main(String[] args) {
        RoomService roomService = RoomService.me();
        roomService.createRoom("123456", "1", "1");
        roomService.joinRoom("123456", "2");
        System.out.println(RoomNotify.me().getRoomListenerMapSize());
        System.out.println("create room 123456");

    }

}
