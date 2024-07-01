package edu.vn.hcmuaf.layer0.handler;

import edu.vn.hcmuaf.layer1.services.ProcessGameService;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

public class ProcessGameHandler implements Subscriber {
    ProcessGameService service = ProcessGameService.me();
    @Override
    public void onOpen(Session session, String... params) {

    }

    @Override
    public void onMessage(Session session, Proto.PacketWrapper message) {
        message.getPacketList().forEach(packet -> {
            if (packet.hasReqStartExam()) {
                System.out.println("ProcessGameHandler : co req start exam");
                session.getAsyncRemote().sendText("da nhan yeu cau req start exam");
                // service start exam
                service.startExam(session,packet.getReqStartExam());
            }
            if(packet.hasReqGetNextQuestion()){
                System.out.println("ProcessGameHandler : co req get next question");
                session.getAsyncRemote().sendText("da nhan yeu cau req get next question");
                // service get next question
                service.nextQuestion(session,packet.getReqGetNextQuestion());
            }
            if(packet.hasReqEndExam()){
                System.out.println("ProcessGameHandler : co req end exam");
                session.getAsyncRemote().sendText("da nhan yeu cau req end exam");
                // service end exam
                service.endExam(session,packet.getReqEndExam());
            }
            if (packet.hasReqCheckQuestionAnswer()){
                System.out.println("ProcessGameHandler : co req check question answer");
                session.getAsyncRemote().sendText("da nhan yeu cau req check question answer");
                // service check question answer
                service.checkQuestionAnswer(session,packet.getReqCheckQuestionAnswer());

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
        return false;
    }
}
