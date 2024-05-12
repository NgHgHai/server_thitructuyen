package edu.vn.hcmuaf.layer2.redis;

import lombok.Getter;
import org.apache.log4j.Logger;

import jakarta.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManage {
//    private static final Logger logger = Logger.getLogger(SessionManage.class);
    private static final SessionManage install = new SessionManage();
    public final Map<String, Session> sessionMap = new ConcurrentHashMap<>();


    @Getter
    private final String endPointID = SessionID.ownerEndPointID;

    private SessionManage() {
//        logger.info("SessionManage created");
        System.out.println("SessionManage created");
    }

    public static SessionManage me() {
        return install;
    }

    public void onOpen(Session session) {
        String sessionId = SessionID.of(session.getId()).getSessionId();
        sessionMap.put(sessionId, session);
    }

    public Map<String, Session> getAll() {
        return sessionMap;
    }

    public List<String> listSessionId() {
        Set<String> strings = sessionMap.keySet();
        ArrayList<String> reList = new ArrayList<>();
        if (strings == null || strings.size() <= 0) {
            return reList;
        }
        reList.addAll(strings);
        return reList;
    }

    public String getSessionID(Session session) {
        return SessionID.of(session.getId()).getSessionId();
    }

    public void onClose(Session session) {
        sessionMap.remove(SessionID.of(session.getId()).getSessionId());
    }

    public void onClose(String sessionId) {
        Session session = sessionMap.remove(sessionId);
    }

    public Session get(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionMap.get(sessionId);
    }

    public int count() {
        return sessionMap.size();
    }


    public void removeSessionId(String sessionID) {
        sessionMap.remove(sessionID);
    }
}
