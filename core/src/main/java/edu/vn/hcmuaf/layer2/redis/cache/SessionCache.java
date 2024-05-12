package edu.vn.hcmuaf.layer2.redis.cache;


import edu.vn.hcmuaf.layer2.CompressUtils;
import edu.vn.hcmuaf.layer2.LogUtils;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.RedisClusterHelper;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import org.apache.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static edu.vn.hcmuaf.layer2.LogUtils.warnIfSlow;


public class SessionCache extends RedisClusterHelper implements ICache<SessionContext> {


    private static final String USER_KEY = SessionContext.class + ":user"; // key of user online in redis (map)
    private static final String USER_WAIT_RELGOIN_KEY = SessionContext.class + ":waitingRelogin:"; // key of user waiting relogin in redis , + them userId
    private static final SessionCache install = new SessionCache();
    private static final ConcurrentHashMap<String, SessionContext> sessionContextMap = new ConcurrentHashMap<>();
    //<UserId,SessionContext> userOnline/
    private static ConcurrentHashMap<String, SessionContext> userOnline = new ConcurrentHashMap<>();
//    Logger logger = Logger.getLogger(SessionCache.class);

    ;




    public static SessionCache me() {
        System.out.println("SessionCache me");
        return install;
    }


    @Override
    public boolean add(String key, SessionContext value) {
        sessionContextMap.put(key, value);
        System.out.println("add key " + key + " " + value);
        return true;
    }

    @Override
    public boolean add(SessionContext value) {
        return this.add(value.getSessionID(), value);
    }

    @Override
    public SessionContext get(String key) {
        long begin = System.currentTimeMillis();
        SessionContext sessionContext = sessionContextMap.get(key);
        if (sessionContext == null) {
            return null;
        }
        if (sessionContext.getUser() == null) return sessionContext;
        sessionContext.setUser(sessionContext.getUser().toBuilder().build());
//        warnIfSlow(logger, begin, 200, "get(String key)");
        return sessionContext;
    }

    public String getSessionIdOfUserOnLocalServer(int userId) {
        for (SessionContext sessionContext : sessionContextMap.values()) {
            if (sessionContext.getUser() != null && sessionContext.getUser().getUserId() == userId) {
                return sessionContext.getSessionID();
            }
        }
        return null;
    }

    @Override
    public List<SessionContext> getAll() {
        return new ArrayList<>(getAllUserOnline().values());
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<>(sessionContextMap.keySet());
    }

    @Override
    public SessionContext remove(String key) {
        SessionContext sessionContext = deleteObj(key);
//        if (sessionContext != null && sessionContext.getUser() != null)
//            logout(sessionContext);
        return sessionContext;
    }

    private SessionContext deleteObj(String key) {
        if (key == null || !sessionContextMap.containsKey(key)) return null;
        System.out.println("co key " + key + " " + sessionContextMap.containsKey(key));
        System.out.println("tien hanh xoa key " + key + " " + sessionContextMap.get(key));
        SessionContext sessionContext = get(key);
        sessionContextMap.remove(key);

        return sessionContext;
    }

    @Override
    public boolean containsKey(String key) {
        return sessionContextMap.containsKey(key);
    }


    @Override
    public void clear() {
        Set<String> keys = getKeys();
        for (String key : keys) {
            remove(key);
        }
    }

    @Override
    public String getKey(SessionContext value) {
        return value.getSessionID();
    }


    public boolean update(SessionContext sessionContext) {
        if (!containsKey(getKey(sessionContext)))
            return false;
        SessionContext sc = get(getKey(sessionContext));
        if (sc == null) {
            return false;
        }
        boolean isUpdate = false;
        if (sc.getUser() != null && sessionContext.getUser() == null) {
            logout(sessionContext);
        }
        if (sc.getUser() == null || !sc.getUser().equals(sessionContext.getUser())) {
            sc.setUser(sessionContext.getUser());
            isUpdate = true;
        }
        if (sc.getRoomId() != sessionContext.getRoomId()) {
            sc.setRoomId(sessionContext.getRoomId());
            isUpdate = true;
        }
        if (isUpdate) sessionContextMap.put(getKey(sessionContext), sessionContext);
        return true;
    }

    public void login(Proto.User user, String sessionID) {
        if (user == null) return;
        addUserOnline(user, sessionID);
    }

    public void logout(SessionContext sessionContext) {
        if (sessionContext == null || sessionContext.getUser() == null) return;
        removeUserOnline(sessionContext.getUser().getUserId());
    }

    public String getSessionId(int userId) {
        SessionContext userOnline = getUserOnline(userId);
        return userOnline == null ? null : userOnline.getSessionID();

    }

    public void addUserOnline(Proto.User user, String sessionId) {
        addUserOnline(user.getUserId(), sessionId);
    }

    public void addUserOnline(int userId, String sessionId) {
        getConnection().hset(USER_KEY.getBytes(), String.valueOf(userId).getBytes(), CompressUtils.compress(sessionContextMap.get(sessionId)));
    }

    public void removeUserOnline(int userId) {
        this.removeUserOnline(String.valueOf(userId));
    }

    public void removeUserOnline(String userId) {
        getConnection().hdel(USER_KEY.getBytes(), userId.getBytes());
    }


    public Map<String, SessionContext> getAllUserOnline() {
        Map<byte[], byte[]> map = getConnection().hgetAll(USER_KEY.getBytes());
        Map<String, SessionContext> result = new HashMap<>();
        map.forEach((k, v) -> {
            SessionContext sessionContext = CompressUtils.decompress(v, SessionContext.class);
            result.put(new String(k), sessionContext);
        });
        return result;
    }

    public void clearMultiUser(Collection<String> userIds) {
        userIds.forEach(this::removeUserOnline);
    }

    public SessionContext getUserOnline(int userId) {
        long begin = System.currentTimeMillis();
        byte[] data = getConnection().hget(USER_KEY.getBytes(), String.valueOf(userId).getBytes());
        if (data == null) return null;
        SessionContext decompress = CompressUtils.decompress(data, SessionContext.class);
//        LogUtils.warnIfSlow(logger, begin, 200, "getUserOnline: cost ");
        return decompress;
    }

    public void updateSessionToCache() {
        if (sessionContextMap == null || sessionContextMap.isEmpty()) return;
        sessionContextMap.forEach((k, v) -> {
            if (v == null || v.getUser() == null) return;
            getConnection().hset(USER_KEY.getBytes(), String.valueOf(v.getUser().getUserId()).getBytes(), CompressUtils.compress(v));
        });
    }

    public void addWaitingReloginList(SessionContext sessionContext) {
        if (sessionContext == null || sessionContext.getUser() == null) return;
        getConnection().set((USER_WAIT_RELGOIN_KEY + sessionContext.getUser().getUserId()).getBytes(), CompressUtils.compress(sessionContext));
        getConnection().expire((USER_WAIT_RELGOIN_KEY + sessionContext.getUser().getUserId()).getBytes(), 60 * 5);
        removeUserOnline(sessionContext.getUser().getUserId());
    }

    public SessionContext getAndRemoveUserInWaitingReloginList(int userId) {
        SessionContext decompress = getUserInWaitingReloginList(userId);
        if (decompress == null) return null;
        getConnection().del((USER_WAIT_RELGOIN_KEY + userId).getBytes());
        return decompress;
    }

    public SessionContext getUserInWaitingReloginList(int userId) {
        byte[] data = getConnection().get((USER_WAIT_RELGOIN_KEY + userId).getBytes());
        if (data == null) return null;
        return CompressUtils.decompress(data, SessionContext.class);
    }


}
