package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.proto.Proto;

import jakarta.websocket.Session;

public interface IService {
    void onOpen(Session session, String... params);

    void onMessage(Session session, Proto.PacketWrapper packetWrapper);

    void onClose(Session session);
}
