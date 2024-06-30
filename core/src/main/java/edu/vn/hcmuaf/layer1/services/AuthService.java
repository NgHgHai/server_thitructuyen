package edu.vn.hcmuaf.layer1.services;


import at.favre.lib.crypto.bcrypt.BCrypt;
import edu.vn.hcmuaf.layer2.dao.Game;
import edu.vn.hcmuaf.layer2.dao.UserDAO;
import edu.vn.hcmuaf.layer2.dao.bean.UserBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import edu.vn.hcmuaf.layer2.sendEmail.EmailHelper;
import jakarta.websocket.Session;

import javax.mail.MessagingException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AuthService {
    //    private static final Logger logger = Logger.getLogger(AuthService.class);
    private static final AuthService instance = new AuthService();
    private static final SessionManage sessionManage = SessionManage.me();
    private final Random r = new Random();

    private AuthService() {
    }

    public static AuthService me() {
        return instance;
    }

    public void processRegister(Session session, Proto.ReqRegister reqRegister) {
        if (reqRegister.getPassword() == null || "".equals(reqRegister.getPassword())) {
            sendMsgRegiter(session, 401);
        }
        if (reqRegister.getUsername() == null || "".equals(reqRegister.getUsername())) {
            sendMsgRegiter(session, 404);
        }
        if (reqRegister.getEmail() == null || "".equals(reqRegister.getEmail())) {
            sendMsgRegiter(session, 404);
        }
        int status = UserDAO.checkUserRegister(reqRegister.getUsername());
        if (status != 200) {
            sendMsgRegiter(session, status);
            return;
        }
        status = UserDAO.checkEmailRegister(reqRegister.getEmail());
        if (status != 200) {
            sendMsgRegiter(session, status);
            return;
        }

        String randomSixDigits = getRamdomSixDigits();
        try {
            EmailHelper.sendVerifyCode(reqRegister.getEmail(), randomSixDigits);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        status = UserDAO.insertRegisterUser(reqRegister.getUsername(), BCrypt.withDefaults().hashToString(12, reqRegister.getPassword().toCharArray()), randomSixDigits);

        sendMsgRegiter(session, status);
    }

    private String getRamdomSixDigits() {
        long currentTimeMillis = System.currentTimeMillis();
        String randomSixDigits = Long.toString(currentTimeMillis).substring(Long.toString(currentTimeMillis).length() - 6);
        return randomSixDigits;
    }


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
//                logger.error("checkRelogin: attempt > 10");
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
        if (userLogin == null || userLogin.getReloginToken() == null || "".equals(userLogin.getReloginToken()) || !userLogin.getReloginToken().equals(packet.getToken()) || userLogin.getActive() != 1) {
            sendLoginWithStatus(session, 403);
            return null;
        }

        String checkLoginInOtherDevice = checkLoginInOtherDevice(userLogin);
        if (Objects.equals(checkLoginInOtherDevice, sessionManage.getSessionID(session))) return null;
        if (checkLoginInOtherDevice != null) {
//            sendMesUserLoginInOtherDevice(checkLoginInOtherDevice);
            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
            resLogin.setStatus(404);
            sendResponse(session, builder.setResLogin(resLogin.build()).build());
            return null;
        }
        sendLoginMsg(session, userLogin, currSessionContext);
        if (currSessionContext.getUser() == null) return null;

//        SessionContext oldSessionContext = SessionCache.me().getAndRemoveUserInWaitingReloginList(currSessionContext.getUser().getUserId());
// neu user cu vo lai ma co phong thi se cho  ho  vao phong  cu
//        if (oldSessionContext != null && oldSessionContext.getUser() != null && Integer.parseInt(oldSessionContext.getRoomId()) > 0) {
//            currSessionContext.setRoomId(oldSessionContext.getRoomId());
//            RoomNotify.me().subscribe(currSessionContext.getRoomId());
//        }

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
        try {
            System.out.println("checkLogin: " + packet.getUsername() + " " + packet.getPassword());
            long begin = System.currentTimeMillis();
            UserBean userLogin = UserDAO.getUserLogin(packet.getUsername());
            Proto.PacketWrapper.Builder builders = Proto.PacketWrapper.newBuilder();
            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
            if (userLogin == null) {
                System.out.println("checkLogin: userLogin null");
            } else {
                System.out.println("checkLogin: userLogin not null");
                System.out.println(userLogin);
            }
            if (userLogin == null || !BCrypt.verifyer().verify(packet.getPassword().getBytes(), userLogin.getPassword().getBytes()).verified) {
                resLogin.setStatus(400);
                sendResponse(session, builder.setResLogin(resLogin.build()).build());
                return;
            }
//
//            LogUtils.warnIfSlow(logger, begin, 300, "checkLogin Time: check DB ");
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
            SessionContext removeSession = SessionCache.me().getAndRemoveUserInWaitingReloginList(userLogin.getId());
//
//            sessionContext.setBot(userLogin.getIsBot() == 1);
//
        } catch (Exception e) {
//            logger.error("Authentication fail! ", e);
            System.out.println(e);
        }
    }

    //
//    // TODO: viết API check user có đang login tại tomcat khác hay không
    private String checkLoginInOtherDevice(UserBean userLogin) {
        String sessionIDInOther = SessionCache.me().getSessionIdOfUserOnLocalServer(userLogin.getId());
        if (sessionIDInOther == null) sessionIDInOther = SessionCache.me().getSessionId(userLogin.getId());
        if (sessionIDInOther == null) return null;
        if (!sessionIDInOther.contains(sessionManage.getEndPointID())) return sessionIDInOther;
        if (sessionIDInOther.contains(sessionManage.getEndPointID()) && sessionManage.get(sessionIDInOther) == null)
            return null;
        Session session1 = sessionManage.get(sessionIDInOther);
        return session1.isOpen() ? sessionIDInOther : null;
    }

//    private void sendMesUserLoginInOtherDevice(String sessionId) {
//        Proto.ResUserAlert.Builder resUserAlert = Proto.ResUserAlert.newBuilder().setStatus(404);
//
//    }

    private void sendLoginMsg(Session session, UserBean userLogin, SessionContext sessionContext) {
        if (sessionContext == null) {
//            logger.error("User Lost connection");
            return;
        }
        Proto.Packet.Builder builder = Proto.Packet.newBuilder();
        Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();
        String reloginToken = UUID.randomUUID().toString();

        UserDAO.updateReloginToken(userLogin.getId(), reloginToken);

        Proto.User user = Proto.User.newBuilder().setUserId(userLogin.getId()).setUsername(userLogin.getUsername()).setPlayerName(Game.StringUtils.defaultIfNull(userLogin.getPlayerName())).setGender(userLogin.getGender()).setEmail(Game.StringUtils.defaultIfNull(userLogin.getEmail())).setPhone(Game.StringUtils.defaultIfNull(userLogin.getPhone())).build();
        //luu thông tin user vào Session context
//        ThreadManage.me().execute(() -> {
        sessionContext.setUser(user);
        SessionCache.me().add(sessionContext);
        SessionCache.me().login(user, sessionManage.getSessionID(session));
//        });

        if ((userLogin.getGender() != 0 && userLogin.getGender() != 1)) resLogin.setStatus(201);
        else resLogin.setStatus(200);
        resLogin.setToken(reloginToken);
        resLogin.setUser(user);
        sendResponse(session, builder.setResLogin(resLogin.build()).build());
    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen()) session.getAsyncRemote().sendObject(packets);
    }


    public void verifyEmail(Session session, Proto.ReqVerify reqVerify) {
        //lay ra user
        SessionContext sessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
        String email = sessionContext.getUser().getEmail();
        //lay code o database ra so sanh
        //--neu giong nhau thi change active = 1

        //xoa code trong database
        //ghi lai thoi gian verify
        //change status cua isVerifyEmail = 1
        if (UserDAO.checkEmailVerify(email, reqVerify.getCode())) {
            UserDAO.doVerifyEmail(email);
        }
        ;
        //tao goi tin resVerifyEmail
        Proto.ResVerify resVerify = Proto.ResVerify.newBuilder().setStatus(200).build();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder().setResVerify(resVerify);
        sendResponse(session, builder.build());
    }

    public void changePassword(Session session, Proto.ReqChangePassword reqChangePassword) {
        //kiem tra mat khau cu trong database
        //neu dung thi update mat khau moi

        try {
            SessionContext sessionContext = SessionCache.me().get(sessionManage.getSessionID(session));
            String username = sessionContext.getUser().getUsername();
            System.out.println("checkLogin: " + username + " " + reqChangePassword.getOldPassword());

            UserBean userLogin = UserDAO.getUserLogin(username);

            Proto.Packet.Builder builder = Proto.Packet.newBuilder();
            Proto.ResLogin.Builder resLogin = Proto.ResLogin.newBuilder();

            if (userLogin == null || !BCrypt.verifyer().verify(reqChangePassword.getOldPassword().getBytes(), userLogin.getPassword().getBytes()).verified) {
                resLogin.setStatus(400);
                sendResponse(session, builder.setResLogin(resLogin.build()).build());
                return;
            }
            UserDAO.changePassword(username, BCrypt.withDefaults().hashToString(12, reqChangePassword.getNewPassword().toCharArray()));

            resLogin.setStatus(200);
            sendResponse(session, builder.setResLogin(resLogin.build()).build());

        } catch (Exception e) {
//            logger.error("Authentication fail! ", e);
            System.out.println(e);
        }
    }

    public void forgotPassword(Session session, Proto.ReqForgotPassword reqForgotPassword) {

        String randomSixDigits = getRamdomSixDigits();
        //tao otp luu voa database
        UserDAO.insertOTP(reqForgotPassword.getEmail(), randomSixDigits);
        //gui otp qua email
        try {
            EmailHelper.sendVerifyCode(reqForgotPassword.getEmail(), randomSixDigits);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        Proto.ResForgotPassword resForgotPassword = Proto.ResForgotPassword.newBuilder().setStatus(200).build();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder().setResForgotPassword(resForgotPassword);
        sendResponse(session, builder.build());
    }

    public void verifyForgotPassword(Session session, Proto.ReqVerifyForgotPassword reqVerifyForgotPassword) {
//    kiem tra xem otp co dung khong
//    neu dung thi gui thong bao xac nhan thanh cong
        int status = UserDAO.checkOTP(reqVerifyForgotPassword.getEmail(), reqVerifyForgotPassword.getOtp());
        Proto.ResVerifyForgotPassword resVerifyForgotPassword = Proto.ResVerifyForgotPassword.newBuilder().setStatus(status).build();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder().setResVerifyForgotPassword(resVerifyForgotPassword);
        sendResponse(session, builder.build());
    }

    public void changePasswordForgot(Session session, Proto.ReqChangePasswordForgot reqChangePasswordForgot) {
//    kiem tra otp co dung khong neu dung thi thay doi pass moi, xoa otp trong database, gui thong bao thay doi pass thanh cong
        int status = UserDAO.checkOTP(reqChangePasswordForgot.getEmail(), reqChangePasswordForgot.getOtp());
        if (status == 200) {
            UserDAO.changePassword(reqChangePasswordForgot.getEmail(), BCrypt.withDefaults().hashToString(12, reqChangePasswordForgot.getPassword().toCharArray()));
        }
        Proto.ResChangePasswordForgot resChangePasswordForgot = Proto.ResChangePasswordForgot.newBuilder().setStatus(status).build();
        Proto.Packet.Builder builder = Proto.Packet.newBuilder().setResChangePasswordForgot(resChangePasswordForgot);
        sendResponse(session, builder.build());
    }

//    public static void main(String[] args) {
//        System.out.println(BCrypt.withDefaults().hashToString(12, "123456".toCharArray()));
//        System.out.println(BCrypt.verifyer().verify("123456".getBytes(), "$2a$12$mtv9AJjQEl6YzC0w2AyljeDmY.19KI3gG4g3aBDEbavVFIMwZ2Nqu".getBytes()).verified);
//        System.out.println(BCrypt.verifyer().verify("123456".getBytes(), "$2a$12$fOat4ELq3HZW.".getBytes()).verified);
//    }


}
