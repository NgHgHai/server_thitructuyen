package edu.vn.hcmuaf.layer2.redis.channel;

import edu.vn.hcmuaf.layer2.CompressUtils;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import jakarta.websocket.Session;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.redisson.api.listener.MessageListener;

import java.io.Serializable;
import java.util.List;

public class PubSubListener implements MessageListener<PubSubListener.Message> {

    @Override
    public void onMessage(CharSequence channel, PubSubListener.Message msg) {
        String ssid = msg.sessionId;
        byte[] content = msg.getContent();
        Proto.PacketWrapper packetWrapper = CompressUtils.decompress(content, Proto.PacketWrapper.class);
        Session session = SessionManage.me().get(ssid);
        List<String> sessions = SessionManage.me().listSessionId();
        if (sessions.contains(ssid) && session != null && session.isOpen()) {
            session.getAsyncRemote().sendObject(packetWrapper);
        }
    }

    @Builder
    @Data
    @ToString
    public static class Message implements Serializable {
        String sessionId;
        byte[] content;
    }
}

