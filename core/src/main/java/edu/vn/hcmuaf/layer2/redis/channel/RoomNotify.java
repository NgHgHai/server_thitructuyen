package edu.vn.hcmuaf.layer2.redis.channel;


import edu.vn.hcmuaf.layer2.CompressUtils;
import edu.vn.hcmuaf.layer2.proto.Proto;

import edu.vn.hcmuaf.layer2.redis.RedisClusterHelper;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomNotify extends RedisClusterHelper {
    private static final RoomNotify install = new RoomNotify();
    private final Map<Integer, PubSubListener> roomListenerMap = new ConcurrentHashMap<>();
    Logger log = Logger.getLogger(RoomNotify.class);

    private RoomNotify() {
    }

    public static RoomNotify me() {
        return install;
    }

    public int getRoomListenerMapSize() {
        return roomListenerMap.size();
    }

    /**
     * Khi create room thi subscribe
     * case user ở 2 tomcat khác nhau, user 2 join rôm thì lúc chưa có listener
     *
     * @param roomId
     */
    public void subscribe(int roomId) {
        if (roomListenerMap.containsKey(roomId)) {
            return;
        }
        PubSubListener listener = new PubSubListener();
        getRedissonClient().getTopic(getIdChannel(roomId)).addListener(PubSubListener.Message.class, listener);
        roomListenerMap.put(roomId, listener);
        log.info("Subscribe room " + getIdChannel(roomId));
        System.out.println("RoomNotify : Subscribe room " + getIdChannel(roomId));
    }

    public void publish(String sessionId, Proto.PacketWrapper packetWrapper, int roomId) {
//        if (!roomListenerMap.containsKey(roomId)) {
//            return;
//        }
        PubSubListener.Message msg = PubSubListener.Message.builder()
                .sessionId(sessionId)
                .content(CompressUtils.compress(packetWrapper))
                .build();
        getRedissonClient().getTopic(getIdChannel(roomId)).publish(msg);
    }

    /**
     * Kh xóa room thì unsubscribe
     *
     * @param roomId
     */
    public void unsubscribe(int roomId) {
        if (!roomListenerMap.containsKey(roomId)) {
            return;
        }
        log.info("Unsubscribe room " + getIdChannel(roomId));
        System.out.println("RoomNotify : Unsubscribe room " + getIdChannel(roomId));
        PubSubListener pubSubListener = roomListenerMap.remove(roomId);
        getRedissonClient().getTopic(getIdChannel(roomId)).removeListener(pubSubListener);
    }

    private String getIdChannel(int roomId) {
        return "ROOM_CHANEL:" + roomId;
    }

}
