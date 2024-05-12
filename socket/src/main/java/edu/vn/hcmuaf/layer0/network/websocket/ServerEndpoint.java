package edu.vn.hcmuaf.layer0.network.websocket;

//import lombok.SneakyThrows;

//import edu.vn.hcmuaf.layer0.handler.AuthHandler;
import edu.vn.hcmuaf.layer0.handler.Subscriber;
import edu.vn.hcmuaf.layer0.handler.*;
//import io.herosnake.layer1.services.LoadingSettingService;
//import io.herosnake.layer1.services.SessionService;
import edu.vn.hcmuaf.layer2.ThreadManage;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.RedisClusterHelper;
//import io.herosnake.layer2.redis.SessionManage;
//import io.herosnake.layer2.redis.cache.ServerStatusCache;
//import io.herosnake.layer2.redis.channel.SystemNotify;
import org.apache.log4j.Logger;

import jakarta.websocket.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@jakarta.websocket.server.ServerEndpoint(value = "/", configurator = CustomEndpointConfigurator.class, decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ServerEndpoint {
//    private static final Logger logger = Logger.getLogger(ServerEndpoint.class);

    private static final Set<Subscriber> subscribers = new HashSet<>();

    public static void init() {
//        logger.info("init socket");
//        SessionService.me().updateServerStatus();

        initLoadingData();

        ServerEndpoint.subscribe(new SessionHandler());
        ServerEndpoint.subscribe(new AuthHandler());
        ServerEndpoint.subscribe(new RoomHandler());
//        ServerEndpoint.subscribe(new NotificationHandler());
//        ThreadManage.me().execute(() -> {
////            while (true) {
//            SystemNotify.me().subscribe();
////            }
//        });
    }

    private static void initLoadingData() {

//        String masterEndpoint = ServerStatusCache.me().getMasterEndpoint();
//        if (!SessionManage.me().getEndPointID().equals(masterEndpoint)) return;
//        LoadingSettingService.me().syncDancingShowSetting();
    }

    public static void destroy() {
//        logger.info("destroy socket");
        RedisClusterHelper.closeConnection();
        subscribers.clear();
//        SystemNotify.me().destroy();
    }

    public static void subscribe(Subscriber sub) {
        subscribers.add(sub);
    }

    //    @SneakyThrows
    @OnOpen
    public void onOpen(Session session) {
        //TODO implement stop new connection if admin set stop new connection (for stopping server)
        //Ask master AI for coding.
        session.setMaxIdleTimeout(60000);
//        subscribers.forEach(s -> ThreadManage.me().execute(() -> s.onOpen(session)));

        subscribers.forEach(s -> s.onOpen(session));

    }

    //    @SneakyThrows
    @OnMessage
    public void onMessage(Session session, Proto.PacketWrapper packetWrapper) throws IOException {
        if(packetWrapper==null || packetWrapper.getPacketList()==null || packetWrapper.getPacketList().isEmpty()) return;
//        boolean login = SessionService.me().checkLogin(session);
        subscribers.forEach(s -> ThreadManage.me().execute(() -> {
            if (!s.requireLogin()) {
                s.onMessage(session, packetWrapper);
                return;
            }
//            if (login) {
//                s.onMessage(session, packetWrapper);
//                return;
//            }
        }));
    }

    @OnClose
    public void onClose(Session session) {
        subscribers.forEach(s -> ThreadManage.me().execute(() -> s.onClose(session)));
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        subscribers.forEach(s -> ThreadManage.me().execute(() -> s.onError(session, throwable)));
    }
}
