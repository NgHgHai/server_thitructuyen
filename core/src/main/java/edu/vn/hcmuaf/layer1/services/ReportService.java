package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.dao.ExamAnswerDAO;
import edu.vn.hcmuaf.layer2.dao.ExamDAO;
import edu.vn.hcmuaf.layer2.dao.UserDAO;
import edu.vn.hcmuaf.layer2.dao.ExamSessionDAO;
import edu.vn.hcmuaf.layer2.dao.bean.ExamSessionBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private static ReportService instance = new ReportService();


    public static ReportService me() {
        return instance;
    }

    private ReportService() {
    }

    public void getAllReportByHostId(Session session, Proto.ReqGetAllReportByHostId reqGetAllReportByHostId) {
        List<ExamSessionBean> reports = ExamSessionDAO.getExamSessionsByHostId(reqGetAllReportByHostId.getHostId());
        Proto.ResGetAllReportByHostId.Builder resGetAllReportByHostId = Proto.ResGetAllReportByHostId.newBuilder();
        List<Proto.Report> reportList = new ArrayList<>();
        for (ExamSessionBean report : reports) {
            Proto.Report.Builder reportBuilder = Proto.Report.newBuilder();

            String examtitle = ExamDAO.getExamById(report.getExamId()).getTitle();

            String hostName = UserDAO.getUserById(report.getHostId()).getUsername();
            reportBuilder.setExamTitle(examtitle);
            reportBuilder.setHostName(hostName);


            reportBuilder.setStartTime(report.getStartTime().toString());
            reportBuilder.setEndTime(report.getEndTime()+"");
            reportBuilder.setExamSessionId(report.getId());
            reportList.add(reportBuilder.build());
        }
        resGetAllReportByHostId.addAllReports(reportList);
        Proto.Packet packet = Proto.Packet.newBuilder().setResGetAllReportByHostId(resGetAllReportByHostId).build();
        sendResponse(session, packet);
        System.out.println("da gui all report by host id");

    }

    public void getAllReportByUserId(Session session, Proto.ReqGetAllReportByPlayerId reqGetAllReportByPlayerId) {
        List<ExamSessionBean> reports = ExamSessionDAO.getExamSessionsByHostId(reqGetAllReportByPlayerId.getUserId());
        Proto.ResGetAllReportByPlayerId.Builder resGetAllReportByPlayerId = Proto.ResGetAllReportByPlayerId.newBuilder();
        List<Proto.Report> reportList = new ArrayList<>();
        for (ExamSessionBean report : reports) {
            Proto.Report.Builder reportBuilder = Proto.Report.newBuilder();

            String examtitle = ExamDAO.getExamById(report.getExamId()).getTitle();

            String hostName = UserDAO.getUserById(report.getHostId()).getUsername();
            reportBuilder.setExamTitle(examtitle);
            reportBuilder.setHostName(hostName);
            reportBuilder.setStartTime(report.getStartTime().toString());
            reportBuilder.setEndTime(report.getEndTime()+"");
        }
        resGetAllReportByPlayerId.addAllReports(reportList);
        Proto.Packet packet = Proto.Packet.newBuilder().setResGetAllReportByPlayerId(resGetAllReportByPlayerId).build();
        sendResponse(session, packet);
        System.out.println("da gui all report by player id");
    }

    public void getReport(Session session, Proto.ReqGetReport reqGetReport) {
        ExamSessionBean report = ExamSessionDAO.getExamSessionById(reqGetReport.getExamSessionId());
        Proto.ResGetReport.Builder resGetReport = Proto.ResGetReport.newBuilder();
        Proto.Report.Builder reportBuilder = Proto.Report.newBuilder();
        String examtitle = ExamDAO.getExamById(report.getExamId()).getTitle();
        String hostName = UserDAO.getUserById(report.getHostId()).getUsername();
        reportBuilder.setExamTitle(examtitle);
        reportBuilder.setHostName(hostName);
        reportBuilder.setStartTime(report.getStartTime().toString());
        reportBuilder.setEndTime(report.getEndTime()+"");
        reportBuilder.setExamSessionId(report.getId());
        //
        Proto.RoomScore.Builder roomScore = Proto.RoomScore.newBuilder();
        Map<String, Integer> userScores;
        userScores = ExamAnswerDAO.getUserScoresBySessionId(reqGetReport.getExamSessionId());
        //
        userScores.forEach((k, v) -> {
            Proto.UserScore.Builder userScore = Proto.UserScore.newBuilder().setUserName(k).setScore(v);
            roomScore.addUserScores(userScore);
        });
        //
        reportBuilder.setRoomScore(roomScore);

        resGetReport.setReport(reportBuilder);

        Proto.Packet packet = Proto.Packet.newBuilder().setResGetReport(resGetReport).build();
        sendResponse(session, packet);
        System.out.println("da gui report");
    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen())
            session.getAsyncRemote().sendObject(packets);
    }
}
