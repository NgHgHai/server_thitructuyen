package edu.vn.hcmuaf.layer0.scheduled.jobs;


import edu.vn.hcmuaf.layer1.services.PingPongServices;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
@DisallowConcurrentExecution
public class PingPongJob implements Job {
    private static Logger logger = Logger.getLogger(PingPongJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            PingPongServices.me().pingPong();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }
}
