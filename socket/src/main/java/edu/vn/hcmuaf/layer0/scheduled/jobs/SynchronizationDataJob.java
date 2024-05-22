//package edu.vn.hcmuaf.layer0.scheduled.jobs;
//
//import edu.vn.hcmuaf.layer2.LogUtils;
//import edu.vn.hcmuaf.layer2.redis.SessionManage;
//import org.apache.log4j.Logger;
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.Job;
//@DisallowConcurrentExecution
//public class SynchronizationDataJob implements Job {
//    private static final Logger logger = Logger.getLogger(SynchronizationDataJob.class);
//
////    private static LoadingSettingService loadingSettingService = LoadingSettingService.me();
//
//
//    @Override
//    public void execute(org.quartz.JobExecutionContext jobExecutionContext) {
//        String masterEndpoint = ServerStatusCache.me().getMasterEndpoint();
//        if (masterEndpoint != null && masterEndpoint.equals(SessionManage.me().getEndPointID())) {
////            action  for master server
//            logger.info("SynchronizationDataJob");
//
//            long begin = System.currentTimeMillis();
//            LogUtils.warnIfSlow(logger, begin, 200, "updateCurrentPoolGoldToDB: time ");
//            LogUtils.warnIfSlow(logger, begin, 200, "SynchronizationDataJob: time ");
//        }
//        //todo other action for each server
//    }
//
//}
