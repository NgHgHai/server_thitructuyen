package edu.vn.hcmuaf.layer2.redis.cache;

import edu.vn.hcmuaf.layer2.redis.JedisServer;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MySessionCache extends JedisServer implements ICache<SessionContext> {
    static MySessionCache instance = new MySessionCache();
    private static final HashMap<String, SessionContext> sessionContextMap = new HashMap<>();

    public static MySessionCache me() {
        return instance;
    }

    @Override
    public boolean add(String key, SessionContext value) {
        sessionContextMap.put(key, value);
        System.out.println(sessionContextMap.get(key));
        getConnection().set(key, value.toString());
        return false;
    }

    @Override
    public boolean add(SessionContext value) {
        return false;
    }

    @Override
    public SessionContext get(String key) {
        return null;
    }

    @Override
    public List<SessionContext> getAll() {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public SessionContext remove(String key) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getKey(SessionContext value) {
        return null;
    }
}
