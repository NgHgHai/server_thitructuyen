package edu.vn.hcmuaf.layer0.handler;

import edu.vn.hcmuaf.layer1.services.ExamService;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

public class ExamHandler implements Subscriber {
    ExamService examService = ExamService.me();
    @Override
    public void onOpen(Session session, String... params) {

    }

    @Override
    public void onMessage(Session session, Proto.PacketWrapper message) {
        message.getPacketList().forEach(packet -> {
            if (packet.hasCreateExamRequest()) {
                System.out.println("ExamHandler : co req Create exam");
                session.getAsyncRemote().sendText("da nhan yeu cau Create exam");
                examService.createExam(session,packet.getCreateExamRequest());
            }
            if (packet.hasUpdateExamRequest()) {
                System.out.println("ExamHandler : co req Update exam");
                session.getAsyncRemote().sendText("da nhan yeu cau Update exam");
                examService.updateExam(session,packet.getUpdateExamRequest());
            }
            if (packet.hasGetExamRequest()) {
                System.out.println("ExamHandler : co req get exam");
                session.getAsyncRemote().sendText("da nhan yeu cau get exam");
                examService.getExam(session,packet.getGetExamRequest());
            }
            if (packet.hasDeleteExamRequest()) {
                System.out.println("ExamHandler : co req delete exam");
                session.getAsyncRemote().sendText("da nhan yeu cau delete exam");
                examService.deleteExam(session,packet.getDeleteExamRequest());
            }
            if (packet.hasGetAllExamRequest()) {
                System.out.println("ExamHandler : co req get all exam");
                session.getAsyncRemote().sendText("da nhan yeu cau get all exam");
                examService.getAllExamByUserId(session,packet.getGetAllExamRequest());
            }
            if (packet.hasCreateQuestionRequest()){
                System.out.println("ExamHandler : co req update exam");
                session.getAsyncRemote().sendText("da nhan yeu cau update exam");
                examService.createQuestion(session,packet.getCreateQuestionRequest());
            }
            if (packet.hasUpdateQuestionRequest()){
                System.out.println("ExamHandler : co req update exam");
                session.getAsyncRemote().sendText("da nhan yeu cau update exam");
                examService.updateQuestion(session,packet.getUpdateQuestionRequest());
            }
            if (packet.hasDeleteQuestionRequest()){
                System.out.println("ExamHandler : co req update exam");
                session.getAsyncRemote().sendText("da nhan yeu cau update exam");
                examService.deleteQuestion(session,packet.getDeleteQuestionRequest());
            }



        });
    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable throwable) {

    }

    @Override
    public boolean requireLogin() {
        return true;
    }
}
