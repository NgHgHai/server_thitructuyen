package edu.vn.hcmuaf.layer0.scheduled.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MsgJob implements Job {

    private static final Logger logger = Logger.getLogger(MsgJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        Proto.ResNotification noti = Proto.ResNotification.newBuilder().build();
//        Proto.Packet packet = Proto.Packet.newBuilder().setResNotification(noti).build();
//        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
//        MsgServices.me().notifyToAll(packetWrapper);
//        logger.info("MsgJob");

    }
}
