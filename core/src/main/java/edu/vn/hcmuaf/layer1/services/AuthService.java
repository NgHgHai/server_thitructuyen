package edu.vn.hcmuaf.layer1.services;


import at.favre.lib.crypto.bcrypt.BCrypt;
import edu.vn.hcmuaf.layer2.LogUtils;
import edu.vn.hcmuaf.layer2.dao.Game;
import edu.vn.hcmuaf.layer2.dao.UserDAO;
import edu.vn.hcmuaf.layer2.dao.bean.UserBean;
import edu.vn.hcmuaf.layer2.proto.Proto;

import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import org.apache.log4j.Logger;

import jakarta.websocket.Session;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class);
    private static final AuthService instance = new AuthService();
    private static final SessionManage sessionManage = SessionManage.me();
    private static final int defaultSponsorId = 123456;
//    private static final UserBean defaultSponsor = UserDAO.selectUser(defaultSponsorId);
    private final Random r = new Random();

    private AuthService() {
    }

    public static AuthService me() {
        return instance;
    }

//    public void processRegister(Session session, Proto.ReqRegister reqRegister) {
//        if (reqRegister.getPassword() == null || "".equals(reqRegister.getPassword())) {
//            sendMsgRegiter(session, 401);
//        }
////        if (reqRegister.getPhone() == null || "".equals(reqRegister.getPhone()) || !ViettelSMS.me().validatePhoneNumber(reqRegister.getPhone())) {
////            sendMsgRegiter(session, 404);
////        }
//        int status = UserDAO.checkUserRegister(reqRegister.getUsername());
//        if (status != 200) {
//            sendMsgRegiter(session, status);
//            return;
//        }
//        UserBean sponsorUser = UserDAO.selectUser(reqRegister.getSponsor());
//        if (reqRegister.getSponsor() != null && !reqRegister.getSponsor().isEmpty() && sponsorUser == null) {
//            sendMsgRegiter(session, 402);
//            return;
//        }
//
//
//        if (defaultSponsor == null) {
//            sendMsgRegiter(session, 403);
//            return;
//        }
//
//        int sponsorId = sponsorUser != null ? sponsorUser.getId() : defaultSponsorId;
//        String tree = sponsorUser != null ? sponsorUser.getTree() : defaultSponsor.getTree();
//        if (tree == null || tree.isEmpty()) {
//            tree = defaultSponsor.getTree() + "," + sponsorId;
//        }
//
//
//        status = UserDAO.insertRegisterUser(reqRegister.getUsername(),
//                BCrypt.withDefaults().hashToString(12, reqRegister.getPassword().toCharArray()),
//                sponsorId == -2 ? defaultSponsorId : sponsorId, reqRegister.getPhone(), "");
//        ViettelSMS.me().sendOTP(reqRegister.getPhone(), "");
//        if (status == 200) {
//            UserDAO.updateTree(reqRegister.getUsername(), tree);
//        }
//        sendMsgRegiter(session, status);
//    }



    private void sendMsgRegiter(Session session, int i) {
        Proto.ResRegister resRegister = Proto.ResRegister.newBuilder().setStatus(i).build();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder().setResRegister(resRegister);
        Proto.PacketWrapper.Builder builders = Proto.PacketWrapper.newBuilder().addPacket(builder);

        sendResponse(session, builder.build());
    }

    public SessionContext checkRelogin(Session session, Proto.ReqRelogin packet) {
        SessionContext currSessionContext;


        int attempt = 0;
        do {
            currSessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
            attempt++;
            if (attempt > 5) {
                logger.error("checkRelogin: attempt > 10");
                sendLoginWithStatus(session, 405);
                return null;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } while (currSessionContext == null);

        UserBean userLogin = UserDAO.getUserLogin(packet.getUsername());
//        if (userLogin.getIsBot()==1 && !((Boolean) session.getUserProperties().get("idPrivateConnect"))) {
//            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
//            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
//            resLogin.setStatus(400);
//            sendResponse(session, builder.setResLogin(resLogin.build()).build());
//            return null;
//        }
        if (userLogin == null || userLogin.getReloginToken() == null || "".equals(userLogin.getReloginToken()) ||
                !userLogin.getReloginToken().equals(packet.getToken()) || userLogin.getActive() != 1) {
            sendLoginWithStatus(session, 403);
            return null;
        }

//        String checkLoginInOtherDevice = checkLoginInOtherDevice(userLogin);
//        if (Objects.equals(checkLoginInOtherDevice, sessionManage.getSessionID(session))) return null;
//        if (checkLoginInOtherDevice != null) {
//            sendMesUserLoginInOtherDevice(checkLoginInOtherDevice);
//
//            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
//            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
//            resLogin.setStatus(404);
//            sendResponse(session, builder.setResLogin(resLogin.build()).build());
//            return null;
//        }
        sendLoginMsg(session, userLogin, currSessionContext);
        if (currSessionContext.getUser() == null) return null;

//        SessionContext oldSessionContext = SessionCache.me().getAndRemoveUserInWaitingReloginList(currSessionContext.getUser().getUserId());
//        if (oldSessionContext != null && oldSessionContext.getUser() != null && oldSessionContext.getRoomId() > 0) {
//            currSessionContext.setRoomId(oldSessionContext.getRoomId());
//            RoomNotify.me().subscribe(currSessionContext.getRoomId());
//        }
//        currSessionContext.setBot(userLogin.getIsBot() == 1);

        return currSessionContext;
    }

    private void sendLoginWithStatus(Session session, int status) {
        Proto.PacketWrapper.Builder builders = Proto.PacketWrapper.newBuilder();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder();
        Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
        resLogin.setStatus(status);
        sendResponse(session, builder.setResLogin(resLogin.build()).build());
    }
//
    public void checkLogin(Session session, Proto.ReqLogin packet) {
//        try {

            long begin = System.currentTimeMillis();
            UserBean userLogin = UserDAO.getUserLogin(packet.getUsername());
            Proto.PacketWrapper.Builder builders = Proto.PacketWrapper.newBuilder();
            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
            if (userLogin == null || !BCrypt.verifyer().verify(packet.getPassword().getBytes(), userLogin.getPassword().getBytes()).verified) {
                resLogin.setStatus(400);
                sendResponse(session, builder.setResLogin(resLogin.build()).build());
                return;
            }
//
            LogUtils.warnIfSlow(logger, begin, 300, "checkLogin Time: check DB ");
            long begin1 = System.currentTimeMillis();
//
            if (userLogin.getActive() == 2) {
                resLogin.setStatus(401);
                sendResponse(session, builder.setResLogin(resLogin.build()).build());
                return;
            }
            if (userLogin.getActive() == 0) {
                resLogin.setStatus(402);
                sendResponse(session, builder.setResLogin(resLogin.build()).build());
                return;
            }
//            logger.info("User " + userLogin.getUsername()+" Login at "+ session.getUserProperties().get("clientIp"));
//
            String sessionId = sessionManage.getSessionID(session);
//
//            logger.info("Send Msg checkLogin" + userLogin.getUsername());
            SessionContext sessionContext = SessionCache.me().get(sessionId);
//            LogUtils.warnIfSlow(logger, begin1, 300, "checkLogin Time: check login in other device ");
//            LogUtils.warnIfSlow(logger, begin, 200, "checkLogin Time: ");
            sendLoginMsg(session, userLogin, sessionContext);
//            remove user waiting relogin; loai bo user trong danh sach doi relogin
//            SessionContext removeSession = SessionCache.me().getAndRemoveUserInWaitingReloginList(userLogin.getId());
//
//            sessionContext.setBot(userLogin.getIsBot() == 1);
//
//        } catch (Exception e) {
//            logger.error("Authentication fail! ", e);
//        }
    }
//
//    // TODO: viết API check user có đang login tại tomcat khác hay không
//    private String checkLoginInOtherDevice(UserBean userLogin) {
//        String sessionIDInOther = SessionCache.me().getSessionIdOfUserOnLocalServer(userLogin.getId());
//        if (sessionIDInOther == null) sessionIDInOther = SessionCache.me().getSessionId(userLogin.getId());
//        if (sessionIDInOther == null) return null;
//        if (!sessionIDInOther.contains(sessionManage.getEndPointID())) return sessionIDInOther;
//        if (sessionIDInOther.contains(sessionManage.getEndPointID()) && sessionManage.get(sessionIDInOther) == null)
//            return null;
//        Session session1 = sessionManage.get(sessionIDInOther);
//        return session1.isOpen() ? sessionIDInOther : null;
//    }

//    private void sendMesUserLoginInOtherDevice(String sessionId) {
//        Proto.ResUserAlert.Builder resUserAlert = Proto.ResUserAlert.newBuilder().setStatus(404);
//
//    }

    private void sendLoginMsg(Session session, UserBean userLogin, SessionContext sessionContext) {
        if (sessionContext == null) {
            logger.error("User Lost connection");
            return;
        }
        Proto.Packet.Builder builder = Proto.Packet.newBuilder();
        Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
        String reloginToken = UUID.randomUUID().toString();

        UserDAO.updateReloginToken(userLogin.getId(), reloginToken);

        Proto.User user = Proto.User.newBuilder()
                .setUserId(userLogin.getId())
                .setUsername(userLogin.getUsername())
                .setPlayerName(Game.StringUtils.defaultIfNull(userLogin.getPlayerName()))
                .setGender(userLogin.getGender())
                .setEmail(Game.StringUtils.defaultIfNull(userLogin.getEmail()))
                .setPhone(Game.StringUtils.defaultIfNull(userLogin.getPhone()))
                .build();
        //luu thông tin user vào Session context
//        ThreadManage.me().execute(() -> {
        sessionContext.setUser(user);
        SessionCache.me().add(sessionContext);
        SessionCache.me().login(user, sessionManage.getSessionID(session));
//        });

        if ((userLogin.getGender() != 0 && userLogin.getGender() != 1))
            resLogin.setStatus(201);
        else
            resLogin.setStatus(200);
        resLogin.setToken(reloginToken);
        resLogin.setUser(user);
        sendResponse(session, builder.setResLogin(resLogin.build()).build());
    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen())
            session.getAsyncRemote().sendObject(packets);
    }
}
