//package edu.vn.hcmuaf.layer0.handler;
//
//import io.herosnake.layer1.services.NotificationService;
//import edu.vn.hcmuaf.layer2.proto.Proto;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
////import org.apache.log4j.Logger;
//
//import jakarta.websocket.Session;
//
//public class NotificationHandler implements Subscriber {
//    private static final Log log = LogFactory.getLog(NotificationHandler.class);
//
//    @Override
//    public void onOpen(Session session, String... params) {
//
//    }
//
//    @Override
//    public void onMessage(Session session, Proto.PacketWrapper message) {
//        try {
//            for (Proto.Packet packet : message.getPacketList()) {
//                onMessage(session, packet);
//            }
//        } catch (Exception e) {
//            log.error("onMessage ", e);
//        }
//    }
//
//    private void onMessage(Session session, Proto.Packet packet) {
////        switch (packet.getDataCase()) {
////            case REQPURCHASEINFO:
////                NotificationService.me().sendNotification(session, packet);
////                break;
////        }
//    }
//
//    @Override
//    public void onClose(Session session) {
//
//    }
//
//    @Override
//    public void onError(Session session, Throwable throwable) {
//
//    }
//
//    @Override
//    public boolean requireLogin() {
//        return true;
//    }
//}
