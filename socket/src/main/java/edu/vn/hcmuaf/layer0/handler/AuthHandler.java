package edu.vn.hcmuaf.layer0.handler;

import edu.vn.hcmuaf.layer1.services.AuthService;
import edu.vn.hcmuaf.layer2.proto.Proto;
//import io.herosnake.layer2.redis.context.SessionContext;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import jakarta.websocket.Session;

public class AuthHandler implements Subscriber {

    private AuthService authService = AuthService.me();

    @Override
    public void onOpen(Session session, String... params) {

    }

    @Override
    public void onMessage(Session session, Proto.PacketWrapper packetWrapper) {
        packetWrapper.getPacketList().forEach(packet -> {
            //to check user đang login trong hệ thống
            if (packet.hasReqLogin()) {
                System.out.println("co req yeu cau login");
                session.getAsyncRemote().sendText("da nhan yeu cau login");
                authService.checkLogin(session, packet.getReqLogin());
            }
            if (packet.hasReqRelogin()) {
                SessionContext sessionContext = authService.checkRelogin(session, packet.getReqRelogin());
//                this.reJoinRoom(sessionContext);
            }
            
            if (packet.hasReqRegister()) {
                authService.processRegister(session, packet.getReqRegister());
            }
            if (packet.hasReqVerify()){
                authService.verifyEmail(session, packet.getReqVerify());
            }
            if (packet.hasReqChangePassword()){
                authService.changePassword(session, packet.getReqChangePassword());
            }
            if (packet.hasReqForgotPassword()){
                authService.forgotPassword(session, packet.getReqForgotPassword());
            }
            if (packet.hasReqVerifyForgotPassword()){
                authService.verifyForgotPassword(session, packet.getReqVerifyForgotPassword());
            }
            if (packet.hasReqChangePasswordForgot()){
                authService.changePasswordForgot(session, packet.getReqChangePasswordForgot());
            }


        });
    }

//    private void reJoinRoom(SessionContext sessionContext) {
//        try {
////            đợi 3s để client load và nhận được mes login
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
