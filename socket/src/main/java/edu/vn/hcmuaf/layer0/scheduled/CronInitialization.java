package edu.vn.hcmuaf.layer0.scheduled;

import edu.vn.hcmuaf.layer0.scheduled.jobs.GCJob;
import edu.vn.hcmuaf.layer0.scheduled.jobs.PingPongJob;

import jakarta.servlet.annotation.WebListener;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
@WebListener
public class CronInitialization implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(CronInitialization.class);
    private final Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

    public CronInitialization() throws SchedulerException {
    }

    //    @SneakyThrows
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            logger.info("Initialized CronInitialization");
            System.out.println("Initialized CronInitialization");
            scheduler.clear();


            /**
             * Adding new jobs here
             */
            scheduler.scheduleJob(createJob(PingPongJob.class), createDefaultTrigger(PingPongJob.class, "0/10 * * * * ?"));
            scheduler.scheduleJob(createJob(GCJob.class), createDefaultTrigger(GCJob.class, "0 0/15 * ? * *"));
//            scheduler.scheduleJob(createJob(MsgJob.class), createDefaultTrigger(MsgJob.class, "0/10 * * * * ?"));
//            scheduler.scheduleJob(createJob(ServerStatusUpdateJob.class), createDefaultTrigger(ServerStatusUpdateJob.class, "0/30 * * ? * *"));
//            scheduler.scheduleJob(createJob(SynchronizationDataJob.class), createDefaultTrigger(SynchronizationDataJob.class, "0 * * ? * *"));


            scheduler.start();
        } catch (SchedulerException e) {
            logger.error("CronInitialization error", e);
            throw new RuntimeException(e);
        }
    }

    //    @SneakyThrows
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            logger.info("Destroyed CronInitialization");
            scheduler.shutdown();
            scheduler.clear();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param c Class extend Job
     * @return JobDetail
     */
    private JobDetail createJob(Class c) {
        return JobBuilder.newJob(c)
                .withIdentity(c.getName())
                .build();
    }

    /**
     * @param c        Class extend Job
     * @param schedule format like that "* * * ? * * *"
     *                 for more information : http://www.quartz-scheduler.org/documentation/quartz-2.3.0/cookbook/WeeklyTrigger.html
     * @return Trigger
     */
    private Trigger createDefaultTrigger(Class c, String schedule) {
        return TriggerBuilder.newTrigger()
                .withIdentity(c.getName())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(schedule))
                .build();
    }
}
