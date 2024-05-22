package edu.vn.hcmuaf.layer2.redis;

import edu.vn.hcmuaf.layer2.CompressUtils;
import edu.vn.hcmuaf.layer2.redis.cache.ICache;
import edu.vn.hcmuaf.layer2.redis.context.RoomContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomRedisClusterHelper extends RedisClusterHelper {
    private static final RoomRedisClusterHelper install = new RoomRedisClusterHelper();
    public static final String ROOM_KEY = ":room:";
    public static final String ROOM_CONTEXT = RoomContext.class + ":roomContext:";

    public static RoomRedisClusterHelper me() {
        if (install == null) {
            return new RoomRedisClusterHelper();
        } else {
            return install;
        }
    }

    public boolean containKey(String roomId) {
        return getConnection().exists((ROOM_KEY + roomId).getBytes());
    }

    public boolean addUsersToRoom(String roomId, String userId) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), userId.getBytes());
        return true;
    }

    public String deleteUserFromRoom(String roomId, String userId) {
        getConnection().srem((ROOM_KEY + roomId).getBytes(), userId.getBytes());
        return userId;
    }

    public boolean addRoomAndRoomContext(String roomId, String userId, RoomContext roomContext) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), userId.getBytes());
        getConnection().hset(ROOM_CONTEXT.getBytes(), roomId.getBytes(), CompressUtils.compress(roomContext));
        return true;
    }

    public boolean addRoom(String roomId, String userId) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), userId.getBytes());
        return true;
    }

    public boolean removeRoom(String roomId, String userId) {
        getConnection().del((ROOM_KEY + roomId).getBytes());
        removeRoomContext(roomId);
        return true;
    }

    public boolean addRoomContext(String roomId, RoomContext roomContext) {
        getConnection().hset(ROOM_CONTEXT.getBytes(), roomId.getBytes(), CompressUtils.compress(roomContext));
        return true;
    }

    public boolean removeRoomContext(String roomId) {
        getConnection().hdel(ROOM_CONTEXT.getBytes(), roomId.getBytes());
        return true;
    }

    public List<String> getAllUserInRoom(String roomId) {
        Set<byte[]> set = getConnection().smembers((ROOM_KEY + roomId).getBytes());
        List<String> result = new java.util.ArrayList<>();
        set.forEach(s -> result.add(new String(s)));
        return result;
    }

    public RoomContext getRoomContext(String roomId) {
        byte[] bytes = getConnection().hget(ROOM_CONTEXT.getBytes(), roomId.getBytes());
        return CompressUtils.decompress(bytes, RoomContext.class);
    }

    public Map<String, RoomContext> getAllRoomContext() {
        Map<byte[], byte[]> map = getConnection().hgetAll(ROOM_KEY.getBytes());
        Map<String, RoomContext> result = new HashMap<>();
        map.forEach((k, v) -> {
            RoomContext roomContext = CompressUtils.decompress(v, RoomContext.class);
            result.put(new String(k), roomContext);
        });
        return result;
    }
}
