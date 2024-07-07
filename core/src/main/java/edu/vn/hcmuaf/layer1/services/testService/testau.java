package edu.vn.hcmuaf.layer1.services.testService;

import edu.vn.hcmuaf.layer1.services.AuthService;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

public class testau {
    public static void main(String[] args) {
        AuthService authService = AuthService.me();
        Proto.ReqVerify reqVerify = Proto.ReqVerify.newBuilder().setCode("123456").build();
        Session session = null;
        authService.verifyEmail(session, reqVerify);
    }
}
