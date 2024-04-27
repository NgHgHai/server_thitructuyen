//package edu.vn.hcmuaf.layer0.handler;
//
//import io.herosnake.layer1.services.SessionService;
//import io.herosnake.layer2.dao.UserDAO;
//import edu.vn.hcmuaf.layer2.proto.Proto;
//import io.herosnake.layer2.redis.SessionManage;
//import io.herosnake.layer2.redis.cache.SessionCache;
//import io.herosnake.layer2.redis.context.SessionContext;
//import org.apache.log4j.Logger;
//
//import jakarta.websocket.Session;
//
//public class SessionHandler implements Subscriber {
//
//    private static Logger logger = Logger.getLogger(SessionHandler.class);
//
//    private SessionService sessionService = SessionService.me();
//
//    @Override
//    public void onOpen(Session session, String... params) {
//        sessionService.onOpen(session, params);
//    }
//
//    @Override
//    public void onMessage(Session session, Proto.PacketWrapper packetWrapper) {
//        packetWrapper.getPacketList().forEach(packet -> {
//            if (packet.hasReqLogout()) {
//                this.logout(session);
//            }
//            if (packet.hasReqUpdateUserInfo()) {
//                sessionService.updateUserInfo(session, packet.getReqUpdateUserInfo());
//            }
//
//        });
//    }
//
//    private void logout(Session session) {
//        String sessionId = SessionManage.me().getSessionID(session);
//        SessionContext sessionContext = SessionCache.me().get(sessionId);
//        if (sessionContext != null && sessionContext.getUser() != null)
//            UserDAO.removeToken(sessionContext.getUser().getUsername());
//        SessionCache.me().logout(sessionContext);
//        clearUserData(session, sessionContext);
//
//        logger.info("Session Logout: Session " + session.getId() + " ; sessionId " + sessionId);
//    }
//
//    private void clearUserData(Session session, SessionContext sessionContext) {
//        if (sessionContext == null) return;
//        if (sessionContext.getUser() == null) {
//            sessionContext.setRoomId(0);
//            SessionCache.me().update(sessionContext);
//            return;
//        }
//        sessionContext.setUser(null);
//        sessionContext.setRoomId(0);
//    }
//
//    @Override
//    public void onClose(Session session) {
//        String sessionID = SessionManage.me().getSessionID(session);
//        logger.info("Session Close: Session " + session.getId() + " ; sessionId " + sessionID);
//        sessionService.onClose(session);
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
