package edu.vn.hcmuaf.layer0.scheduled.jobs;


import edu.vn.hcmuaf.layer1.services.SessionService;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import jakarta.websocket.Session;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@DisallowConcurrentExecution
public class ServerStatusUpdateJob implements Job {

    private static final Logger logger = Logger.getLogger(ServerStatusUpdateJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("ServerStatusUpdateJob");
//        SessionService.me().updateServerStatus();
//        SessionService.me().clearClosingServer();
        clearClosingSession();

    }

    public void clearClosingSession() {
        Set<String> keyList = new HashSet<>(SessionManage.me().listSessionId());
        Set<String> userOnlineOfEndpoints = SessionCache.me().getAllUserOnline().keySet().stream().filter(key -> key.contains(SessionManage.me().getEndPointID())).collect(Collectors.toSet());
        keyList.addAll(userOnlineOfEndpoints);
        keyList.forEach(sessionId -> {
            Session session = SessionManage.me().get(sessionId);
            if (session == null || !session.isOpen()) {
                SessionContext sessionContext = SessionCache.me().get(sessionId);
                if (sessionContext == null) {
                    SessionManage.me().onClose(sessionId);
                    return;
                }
//                không cần dùng đoạn code này thì ClearRoomJob sẽ làm
//                if (sessionContext.getRoomId() > 0 && RoomCache.me().containsKey(String.valueOf(sessionContext.getRoomId()))) {
//                    RoomService.me().leaveRoomUserDisconnect(sessionContext);
//                }
                logger.error("Clear Closing Session: " + sessionId);
                SessionCache.me().remove(sessionId);
                SessionManage.me().onClose(sessionId);
            }
        });
        List<String> keyList1 = SessionManage.me().listSessionId();
        SessionCache.me().getKeys().forEach(key -> {
            if (!keyList1.contains(key) && key.contains(SessionManage.me().getEndPointID())) {
                SessionCache.me().remove(key);
            }
        });
    }
}
