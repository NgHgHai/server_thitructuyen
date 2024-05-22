package edu.vn.hcmuaf.layer1.services;


import edu.vn.hcmuaf.layer2.redis.SessionManage;
import org.apache.log4j.Logger;

import jakarta.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class PingPongServices {
    private static final Logger logger = Logger.getLogger(PingPongServices.class);
    private static final PingPongServices install = new PingPongServices();
    SessionManage sessionManage = SessionManage.me();

    private PingPongServices() {
    }

    public static PingPongServices me() {
        return install;
    }

    public void pingPong() {
        List<String> keyList = sessionManage.listSessionId();
        logger.info("pingPongJob: numOfClient "+keyList.size());
        for (String sessionId : keyList) {
            Session session = sessionManage.get(sessionId);
            try {
                if (session != null && session.isOpen())
                    session.getAsyncRemote().sendPing(ByteBuffer.wrap("ping".getBytes()));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
//        remoweList.forEach(s -> sessionManage.addSessionIDToRemoveList(s));

    }

}
