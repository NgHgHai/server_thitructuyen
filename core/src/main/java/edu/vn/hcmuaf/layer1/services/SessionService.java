package edu.vn.hcmuaf.layer1.services;


import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.cache.MySessionCache;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import edu.vn.hcmuaf.layer2.dao.UserDAO;
import org.apache.log4j.Logger;


import jakarta.websocket.Session;

import java.util.*;

public class SessionService implements IService {

    private static final Logger logger = Logger.getLogger(SessionService.class);
    private static final SessionService install = new SessionService();

    private final SessionManage sessionManage = SessionManage.me();

    private SessionService() {
    }

    public static SessionService me() {
        return install;
    }

    public void onOpen(Session session, String... params) {
        sessionManage.onOpen(session);
        String sessionID = sessionManage.getSessionID(session);
        //loi o dong nay
        SessionContext sessionContext = SessionContext.builder().sessionID(sessionID).socketID(sessionManage.getEndPointID()).build();
        System.out.println(sessionContext);
        logger.info("Session Open: Open - " + session.getId() + " ; all - " + sessionManage.count() + " UUID " + sessionID);
        System.out.println("Session Open: Open - " + session.getId() + " ; all - " + sessionManage.count() + " UUID " + sessionID);
        try {
            session.getBasicRemote().sendText("this is your ssid : " + sessionID);
            MySessionCache.me().add(sessionID, sessionContext);
        } catch (Exception e) {
            System.out.println("loi o day");
        }

    }

    public void onMessage(Session session, Proto.PacketWrapper packetWrapper) {
    }


    public void onClose(Session session) {
        String sessionID = sessionManage.getSessionID(session);
        sessionManage.removeSessionId(sessionID);
//        SessionCache.me().addWaitingReloginList(sessionContext);
//        SessionCache.me().remove(sessionID);
    }

//    public void updateServerStatus() {
//        ServerStatusCache.me().updateEndPointStatus(sessionManage.getEndPointID());
//    }

//    public void clearClosingServer() {
//        Map<String, Long> allEndPointID = ServerStatusCache.me().getAllEndPointID();
//        List<String> onlineEndpoints = new ArrayList<>();
//        allEndPointID.keySet().stream()// nếu giá trị lớn 1 phút 10s thì xóa tất cả session của endpoint đó
//                .forEach(endPointID -> {
//                    if (System.currentTimeMillis() - allEndPointID.get(endPointID) > 70000) {
//                        logger.error("Clear Closing Server: " + endPointID);
//                        ServerStatusCache.me().removeEndPoint(endPointID);
//                    } else {
//                        onlineEndpoints.add(endPointID);
//                    }
//                });
//        Map<String, SessionContext> allUser = SessionCache.me().getAllUserOnline();
//        Set<String> userOnline = new HashSet<>();
//        allUser.forEach((key, value) -> {
//            onlineEndpoints.stream().anyMatch(endPoint -> {
//                if (value.getSessionID().contains(endPoint)) {
//                    userOnline.add(key);
//                    return true;
//                }
//                return false;
//            });
//        });
//        Set<String> offlineUser = allUser.keySet();
//        offlineUser.removeAll(userOnline);
//        SessionCache.me().clearMultiUser(offlineUser);
//    }

//    public boolean checkLogin(Session session) {
//        SessionContext sessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
//        if (sessionContext == null) {
//            return false;
//        }
//        return sessionContext.getUser() != null;
//    }


//    public void sendUserInfo(Session session) {
//        SessionContext sessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
////        Proto.ResUserInfo resUserInfo = Proto.ResUserInfo.newBuilder().setUser(sessionContext.getUser()).build();
////        session.getAsyncRemote().sendObject(
////                Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResUserInfo(resUserInfo)).build()
////        );
//    }

//    public void updateUserInfo(Session session, Proto.ReqUpdateUserInfo reqUpdateUserInfo) {
//        SessionContext sessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
//        Proto.User.Builder user = sessionContext.getUser().toBuilder();
//        user.setPlayerName(reqUpdateUserInfo.getPlayerName().equals("") ? user.getPlayerName() : reqUpdateUserInfo.getPlayerName());
//        user.setGender(reqUpdateUserInfo.getGender());
//        sessionContext.setUser(user.build());
//        UserDAO.updateUserInfo(sessionContext.getUser());
//        SessionCache.me().update(sessionContext);
////        session.getAsyncRemote().sendObject(
////                Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder()
////                        .setResUserInfo(Proto.ResUserInfo.newBuilder()
////                                .setUser(sessionContext.getUser()))).build()
////        );
//    }

//    public void verifyPhoneNumberStep1(Session session, Proto.ReqVerifyPhoneNumberStep1 reqVerifyPhoneNumberStep1) {
//
//        String sessionID = sessionManage.getSessionID(session);
//        SessionContext sessionContext = SessionCache.me().get(sessionID);
//        Proto.User user = sessionContext.getUser();
//        //        int otp = r.nextInt(100000, 999999);
//        int otp = 123456;//TODO: remove khi gửi được OPT thực
//
//        UserDAO.updatePhongNumberAndOTP(user.getUserId(), reqVerifyPhoneNumberStep1.getPhone(), otp);
//
//
//    }
//
//    public void verifyPhoneNumberStep2(Session session, Proto.ReqVerifyPhoneNumberStep2 reqVerifyPhoneNumberStep2) {
//    }
}

