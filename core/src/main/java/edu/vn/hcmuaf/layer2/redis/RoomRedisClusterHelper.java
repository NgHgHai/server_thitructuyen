package edu.vn.hcmuaf.layer2.redis;

import edu.vn.hcmuaf.layer2.CompressUtils;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.context.RoomContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoomRedisClusterHelper extends RedisClusterHelper {
    private static final RoomRedisClusterHelper install = new RoomRedisClusterHelper();
    public static final String ROOM_KEY = ":room:";
    public static final String ROOM_CONTEXT = RoomContext.class + ":roomContext:";
    private static final String ROOM_SCORE = ":roomScore:";

    public static RoomRedisClusterHelper me() {
        if (install == null) {
            return new RoomRedisClusterHelper();
        } else {
            return install;
        }
    }

    public boolean containKey(int roomId) {
        return getConnection().exists((ROOM_KEY + roomId).getBytes());
    }

    public boolean addUsersToRoom(int roomId, String sessionId) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), sessionId.getBytes());
        System.out.println("RoomRedisClusterHelper : da them user vao phong voi id : " + roomId + " va session id : " + sessionId);
        return true;
    }

    public boolean deleteUserFromRoom(int roomId, String sessionId) {
        getConnection().srem((ROOM_KEY + roomId).getBytes(), sessionId.getBytes());
        System.out.println("RoomRedisClusterHelper : da xoa user khoi phong voi id : " + roomId + " va session id : " + sessionId);
        return true;
    }

    public boolean addRoomAndRoomContext(int roomId, String hostSessionId, RoomContext roomContext) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), hostSessionId.getBytes());
        getConnection().hset(ROOM_CONTEXT.getBytes(), String.valueOf(roomId).getBytes(), CompressUtils.compress(roomContext));
        System.out.println("RoomRedisClusterHelper : da tao phong voi id : " + roomId + " va chu phong : " + hostSessionId);
        return true;
    }

    public boolean addRoom(int roomId, int userId) {
        getConnection().sadd((ROOM_KEY + roomId).getBytes(), String.valueOf(userId).getBytes());
        return true;
    }

    public boolean removeRoom(int roomId) {
        getConnection().del((ROOM_KEY + roomId).getBytes());
        removeRoomContext(roomId);
        System.out.println("RoomRedisClusterHelper : da xoa phong voi id : " + roomId);
        return true;
    }

    public boolean addRoomContext(int roomId, RoomContext roomContext) {
        getConnection().hset(ROOM_CONTEXT.getBytes(), String.valueOf(roomId).getBytes(), CompressUtils.compress(roomContext));
        return true;
    }

    public boolean removeRoomContext(int roomId) {
        getConnection().hdel(ROOM_CONTEXT.getBytes(), String.valueOf(roomId).getBytes());
        return true;
    }

    public List<String> getAllUserInRoom(int roomId) {
        Set<byte[]> set = getConnection().smembers((ROOM_KEY + roomId).getBytes());
        List<String> result = new java.util.ArrayList<>();
        set.forEach(s -> result.add(new String(s)));
        return result;
    }

    public RoomContext getRoomContext(int roomId) {
        byte[] bytes = getConnection().hget(ROOM_CONTEXT.getBytes(), String.valueOf(roomId).getBytes());
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

    public Map<Integer, Integer> getRoomScore(int roomid) {
        Map<byte[], byte[]> map = getConnection().hgetAll((ROOM_SCORE + roomid).getBytes());
        Map<Integer, Integer> result = new HashMap<>();
        if (map.isEmpty()) return null;
        map.forEach((k, v) -> {
            result.put(Integer.parseInt(new String(k)), Integer.parseInt(new String(v)));
        });
        return result;
    }

    public void saveRoomScore(Map<Integer, Integer> userScores, int roomid) {
        userScores.forEach((k, v) -> {
            getConnection().hset((ROOM_SCORE + roomid).getBytes(), String.valueOf(k).getBytes(), String.valueOf(v).getBytes());
        });
    }

    public boolean removeRoomScore(int roomid) {
        getConnection().del((ROOM_SCORE + roomid).getBytes());
        return true;
    }
}
