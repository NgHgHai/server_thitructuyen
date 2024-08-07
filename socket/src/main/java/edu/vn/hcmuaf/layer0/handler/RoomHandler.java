package edu.vn.hcmuaf.layer0.handler;

import edu.vn.hcmuaf.layer1.services.RoomService;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

public class RoomHandler implements Subscriber {
    RoomService roomService = RoomService.me();
    @Override
    public void onOpen(Session session, String... params) {

    }

    @Override
    public void onMessage(Session session, Proto.PacketWrapper message) {
        message.getPacketList().forEach(packet -> {
            if (packet.hasReqCreateRoom()) {
//                System.out.println("RoomHandler : co req create room");
                session.getAsyncRemote().sendText("da nhan yeu cau create room");
                //logic create room
                Proto.ReqCreateRoom reqCreateRoom = packet.getReqCreateRoom();
                roomService.createRoom(session,reqCreateRoom);
            }
            if (packet.hasReqJoinRoom()) {
//                System.out.println("RoomHandler : co req join room");
                session.getAsyncRemote().sendText("da nhan yeu cau join room");
                roomService.joinRoom(packet.getReqJoinRoom().getRoomId(),session);

            }
            if (packet.hasReqOutRoom()) {
//                System.out.println("RoomHandler : co req leave room");
                session.getAsyncRemote().sendText("da nhan yeu cau leave room");
                roomService.outRoom(packet.getReqOutRoom().getRoomId(),session);
            }
            if (packet.hasReqCloseRoom()) {
                //close room
            }
        });
    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable throwable) {

    }

    @Override
    public boolean requireLogin() {
        return false;
    }
}
