package edu.vn.hcmuaf.layer0.handler;

import edu.vn.hcmuaf.layer2.proto.Proto;

import jakarta.websocket.Session;

public interface Subscriber {
    void onOpen(Session session, String... params);

    void onMessage(Session session, Proto.PacketWrapper message);

    void onClose(Session session);

    void onError(Session session, Throwable throwable);

    boolean requireLogin();
}
