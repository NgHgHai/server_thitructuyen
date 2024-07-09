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

    public void createRoom(Session session, Proto.ReqCreateRoom packet) {
        /**
         * client se gui thong diep proto.proto toi RoomHandler,
         * sau do RoomHandler se goi tiep toi RoomService,
         * o day no se tu tao ra 1 roomID (6 so), goi toi SessionManage truyen vao sesion(WS) de lay ra hostSsid sau do tao ra RoomContext .
         * Sau do se luu 1 set co key la roomid vao redis va luu vao redis 1 hset key la RoomContext de luu thong tin fille la roomid va value la roomcoontext.
         * sau do se goi toi roomNotify de dang ki lang nghe 1 channel co ten la roomid de lang nghe thong tin tu cac server khac.
         * sau khi xong tao 1 goi tin resCreateRoom chua id room va gui lai cho client thong qua session(WS) luc dau
         */
        //gen id 6 so  int
        int roomId = genIdRoom();

        String hostSessionId = SessionManage.me().getSessionID(session);

        // tao room context
        RoomContext roomContext = RoomContext.builder().roomId(roomId).hostId(packet.getUserId()).testId(packet.getTestId()).build();
        // tao room
        createRoom(roomId, hostSessionId, roomContext);

        // send response
        sendResponse(session, Proto.Packet.newBuilder().setResCreateRoom(Proto.ResCreateRoom.newBuilder().setRoomId(roomId).build()).build());
        session.getAsyncRemote().sendText(roomId + "");

    }

    private int genIdRoom() {
        String id = String.valueOf(System.currentTimeMillis()).substring(7);
        return roomRedisClusterHelper.containKey(Integer.parseInt(id)) ? genIdRoom() : Integer.parseInt(id);
    }

    public void createRoom(int roomId, String hostSessionId, RoomContext roomContext) {
        // add room va room context vao redis
        roomRedisClusterHelper.addRoomAndRoomContext(roomId, hostSessionId, roomContext);
        // dang ki xuong redis 1 channel, lang nghe channel do
        roomNotify.subscribe(roomId);
        System.out.println("RoomService : da tao phong voi id : " + roomId + " va chu phong : " + hostSessionId);
    }

    public void joinRoom(int roomId, Session session) {
        /**
         * Hanh dong tham gia phong:
         * client se gui thong diep proto.proto toi RoomHandler, sau do RoomHandler se goi tiep toi RoomService,
         * o day no se lay ra roomID tu goi tin, goi toi SessionManage truyen vao sesion(WS) de lay ra ssid sau do
         * nho vao ssid ma goi toi SessionCache de lay ra SessionContext cua user nay, thay doi lai thong tin phong trong SessionContext
         * va update lai vao SessionCache. Sau do lay ra toan bo ssid nguoi online trong server nho vao goi ham o SessionManage.
         * Sau do lay ra tat ca ssid nguoi choi trong phong tu redis. Tao ra 1 goi tin reqJoinRoom,
         * sau do neu nguoi choi trong phong thuoc server thi se lay ra session(WS) thong qua SessionManage va gui goi tin di,
         * con neu nguoi trong phong khong thuoc server thi se publish 1 thong diep xuong channel cua phong do o redis
         */

        String sessionId = SessionManage.me().getSessionID(session);
        // lay ra session context
        SessionContext sessionContext = sessionCache.get(sessionId);
        if (sessionContext.getUser() == null) {
            System.out.println("User null");
            Proto.ResJoinRoom resJoinRoom1 = Proto.ResJoinRoom.newBuilder().setStatus(400).build();
            Proto.Packet packet1 = Proto.Packet.newBuilder().setResJoinRoom(resJoinRoom1).build();
            Proto.PacketWrapper packetWrapper1 = Proto.PacketWrapper.newBuilder().addPacket(packet1).build();
            session.getAsyncRemote().sendObject(packetWrapper1);
            return;
        }
        // set room id
        sessionContext.setRoomId(roomId);

        sessionCache.add(sessionId, sessionContext);// add local cache
        sessionCache.update(sessionContext);// update redis


        // lay ra toan bo session dang online trong server nay
        Set<String> sessionListInServer = SessionCache.me().getKeys();
        System.out.printf("RoomService : sessionListInServer : %s\n", sessionListInServer.toString());
        // them session vao room
        roomRedisClusterHelper.addUsersToRoom(roomId, sessionId);
        System.out.printf("RoomService : da them user vao phong voi id : %d va session id : %s\n", roomId, sessionId);
        // dang ki lang nghe channel room ma session vua join
        roomNotify.subscribe(roomId);
        //gethostId
        String hostId = String.valueOf(roomRedisClusterHelper.getRoomContext(roomId).getHostId());

        // lay ra toan bo sessionId trong phong tu redis
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);


        // tao goi tin resJoinRoom

//        Proto.ResJoinRoom resJoinRoom = Proto.ResJoinRoom.newBuilder().setName(sessionContext.getUser().getUsername()).setSessionId(sessionId).build();
        Proto.ResJoinRoom resJoinRoom = Proto.ResJoinRoom.newBuilder().setName(sessionContext.getUser().getUsername()).setStatus(200).setSessionId(sessionId).setHostId(hostId).setTotalPlayer(sessionList.size()).build();
        System.out.println("RoomService : resJoinRoom : " + resJoinRoom);
        Proto.Packet packet = Proto.Packet.newBuilder().setResJoinRoom(resJoinRoom).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

//        session.getAsyncRemote().sendText("Da vao phong" + sessionContext.getUser().getUsername());
        // thong bao cho tat ca moi nguoi trong phong


        System.out.printf("RoomService : sessionList user im room: %s\n", sessionList.toString());
        // gui goi tin toi tat ca moi nguoi trong phong neu online trong server nay
        // neu khong thi publish xuong redis de cac server khac biet
        sessionList.forEach(s -> {

            if (sessionListInServer.contains(s)) {
                Session session1 = SessionManage.me().get(s);
//                session1.getAsyncRemote().sendText("Co " + sessionId + " moi vao  phong dưedadas");
                // gui goi tin toi session
                session1.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });

        System.out.println("session : " + sessionId + " da vao phong : " + roomId);
    }

    public void outRoom(int roomId, Session session) {
        String sessionId = SessionManage.me().getSessionID(session);
        // lay ra session context
        SessionContext sessionContext = sessionCache.get(sessionId);
        // set room id
        sessionContext.setRoomId(0);

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
        // lay ra toan bo sessionId trong phong tu redis
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);
        // gui goi tin toi tat ca moi nguoi trong phong neu online trong server nay
        // neu khong thi publish xuong redis de cac server khac biet
        sessionList.forEach(s -> {

            if (sessionListInServer.contains(s)) {
                Session session1 = SessionManage.me().get(s);
                session1.getAsyncRemote().sendText("Co " + sessionId + " thoat phong");
                // gui goi tin toi session
                session1.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });

        System.out.println("session : " + sessionId + " da thoat khoi phong : " + roomId);
    }

    public void closeRoom(String token, int roomId) {
//        // lay ra room context
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(roomId);
//        // kiem tra token
        if (!UserDAO.checkToken(token, roomContext.getHostId())) {
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
                System.out.println("publish to redis user has session : " + s + "close room" + roomId);
            }
        });
//      xoa room va room context trong redis
        roomRedisClusterHelper.removeRoom(roomId);
//      Thong bao voi cac server khac ve thong tin phong co roomid da bi dong
//        Proto.PacketWrapper reqCloseRoomPakageWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setReqCloseRoom(Proto.ReqCloseRoom.newBuilder().setRoomId(roomId).build()).build()).build();
//        MainChannelNotify.me().publish(reqCloseRoomPakageWrapper);
//
        System.out.println("phong : " + roomId + " da bi xoa");

    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen())
            session.getAsyncRemote().sendObject(packets);
    }

    public static void main(String[] args) {
//        RoomService roomService = RoomService.me();
//        roomService.createRoom("123456", "1", "1");
//        roomService.joinRoom("123456", "2");
//        System.out.println(RoomNotify.me().getRoomListenerMapSize());
//        System.out.println("create room 123456");

    }

}
