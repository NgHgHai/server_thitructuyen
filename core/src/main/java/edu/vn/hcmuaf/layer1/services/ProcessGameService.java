package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.dao.*;
import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamAnswerBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamSessionBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.RoomRedisClusterHelper;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import edu.vn.hcmuaf.layer2.redis.channel.RoomNotify;
import edu.vn.hcmuaf.layer2.redis.context.RoomContext;
import jakarta.websocket.Session;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessGameService extends PoolConnectDAO {
    public static final ProcessGameService instance = new ProcessGameService();
    public static final SessionManage sessionManage = SessionManage.me();
    public static final RoomRedisClusterHelper roomRedisClusterHelper = RoomRedisClusterHelper.me();
    public static final RoomNotify roomNotify = RoomNotify.me();

    public static ProcessGameService me() {
        return instance;
    }

    Jdbi jdbi = getJdbi();

    public void startExam(Session session, Proto.ReqStartExam reqStartExam) {
        ExamSessionBean examSessionBean = new ExamSessionBean();
        examSessionBean.setExamId(reqStartExam.getExamId());
        examSessionBean.setHostId(reqStartExam.getHostId());
        examSessionBean.setStatus(1);
        examSessionBean.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));

        int examSessionId = jdbi.onDemand(IExamSessionDAO.class).insertExamSession(examSessionBean);
        // lay ra roomContext tu redis
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(reqStartExam.getRoomId());
        // set cac thong tin cua roomContext
        roomContext.setExamSessionId(examSessionId);

        roomContext.setHostId(reqStartExam.getHostId());//lay tam, dang le phai lay tu sessioncontext
        roomContext.setTestId(reqStartExam.getExamId());//lay tam

        roomContext.setStatus(1);
        List<QuestionBean> questionBeans = jdbi.onDemand(IQuestionDAO.class).getQuestionsByExamId(reqStartExam.getExamId());
        // lay ra cau hoi dau tien
        QuestionBean firstQuestion = jdbi.onDemand(IQuestionDAO.class).getCompleteQuestionById(questionBeans.get(0).getId(), jdbi.onDemand(IChoiceDAO.class));
        // cap nhat thong tin ve cau hoi hien tai cua roomContext cho redis
        roomContext.setCurrentQuestionId(firstQuestion.getId());
        roomContext.setCurrentquestionIndex(firstQuestion.getQuestionIndex());
        // luu lai roomContext vao redis
        roomRedisClusterHelper.addRoomContext(roomContext.getRoomId(), roomContext);

        // tao goi tin tra ve cho client
        Proto.ResStartExam.Builder resStartExam = Proto.ResStartExam.newBuilder().setExamSessionId(examSessionId);
        Proto.QuestionResponse.Builder questionResponse = Proto.QuestionResponse.newBuilder().setQuestionId(firstQuestion.getId()).setQuestionIndex(firstQuestion.getQuestionIndex()).setQuestionText(firstQuestion.getQuestionText()).setImageUrl(firstQuestion.getImageUrl()).setTime(firstQuestion.getTime());


        Proto.Packet packetResExam = Proto.Packet.newBuilder().setResStartExam(resStartExam).build();
        Proto.Packet packetQuestion = Proto.Packet.newBuilder().setQuestionResponse(questionResponse).build();

        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packetResExam).addPacket(packetQuestion).build();


        // gui thong tin ve cho clients
        sendForAllSessionInRoom(packetWrapper, reqStartExam.getRoomId());


    }

    public void nextQuestion(Session session, Proto.ReqGetNextQuestion reqGetNextQuestion) {
        // lay ra roomContext tu redis
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(reqGetNextQuestion.getRoomId());
        // lay ra danh sach cau hoi tu db
        List<QuestionBean> questionBeans = jdbi.onDemand(IQuestionDAO.class).getQuestionsByExamId(roomContext.getTestId());
        // lay ra cau hoi tiep theo
        QuestionBean nextQuestion = roomContext.getCurrentquestionIndex() < questionBeans.size() - 1 ? jdbi.onDemand(IQuestionDAO.class).getCompleteQuestionById(questionBeans.get(roomContext.getCurrentquestionIndex() + 1).getId(), jdbi.onDemand(IChoiceDAO.class)) : null;
        // cap nhat thong tin ve cau hoi hien tai cua roomContext cho redis
        roomContext.setCurrentQuestionId(nextQuestion.getId());
        roomContext.setCurrentquestionIndex(nextQuestion.getQuestionIndex());
        // luu lai roomContext vao redis
        roomRedisClusterHelper.addRoomContext(roomContext.getRoomId(), roomContext);

        // tao goi tin tra ve cho client
        Proto.QuestionResponse questionResponse = Proto.QuestionResponse.newBuilder().setQuestionId(nextQuestion.getId()).setQuestionIndex(nextQuestion.getQuestionIndex()).setQuestionText(nextQuestion.getQuestionText()).setImageUrl(nextQuestion.getImageUrl()).setTime(nextQuestion.getTime()).build();
        Proto.ResRoomScore resRoomScore = getRoomScore(reqGetNextQuestion.getRoomId());

        Proto.Packet packetQuestion = Proto.Packet.newBuilder().setQuestionResponse(questionResponse).build();
        Proto.Packet packetResRoomScore = Proto.Packet.newBuilder().setResRoomScore(resRoomScore).build();

        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packetQuestion).addPacket(packetResRoomScore).build();

        // gui thong tin ve cho client
        sendForAllSessionInRoom(packetWrapper, reqGetNextQuestion.getRoomId());
    }

    public void endExam(Session session, Proto.ReqEndExam reqEndExam) {
        // lay ra roomContext tu redis
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(reqEndExam.getRoomId());
        // cap nhat trang thai cua roomContext
        roomContext.setStatus(0);
        roomContext.setExamSessionId(0);
        roomContext.setCurrentquestionIndex(0);
        roomContext.setCurrentQuestionId(0);
        // luu lai roomContext vao redis
        roomRedisClusterHelper.addRoomContext(roomContext.getRoomId(), roomContext);

        // tao goi tin tra ve cho client
        Proto.ResEndExam.Builder resEndExam = Proto.ResEndExam.newBuilder().setResRoomScore(getRoomScore(reqEndExam.getRoomId()));
        Proto.Packet packet = Proto.Packet.newBuilder().setResEndExam(resEndExam).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // gui thong tin ve cho client
        sendForAllSessionInRoom(packetWrapper, reqEndExam.getRoomId());
    }

    public void checkQuestionAnswer(Session session, Proto.ReqCheckQuestionAnswer reqCheckQuestionAnswer) {
        // luu cau tra loi cua nguoi choi vao db
        ExamAnswerBean examAnswerBean = new ExamAnswerBean();
        examAnswerBean.setExamSessionId(reqCheckQuestionAnswer.getExamSessionId());
        examAnswerBean.setQuestionId(reqCheckQuestionAnswer.getQuestionId());
        examAnswerBean.setChoiceId(reqCheckQuestionAnswer.getChoiceId());
        jdbi.onDemand(IExamAnswerDAO.class).insertExamAnswer(examAnswerBean);


        //check xem cau tra loi dung hay sai
        QuestionBean questionBean = jdbi.onDemand(IQuestionDAO.class).getCompleteQuestionById(reqCheckQuestionAnswer.getQuestionId(), jdbi.onDemand(IChoiceDAO.class));
        List<ChoiceBean> choiceBeans = questionBean.getChoices();
        boolean isCorrect = false;
        for (ChoiceBean choiceBean : choiceBeans) {
            if (choiceBean.getId() == reqCheckQuestionAnswer.getChoiceId() && choiceBean.isCorrect()) {
                isCorrect = true;
                break;
            }
        }

        // xoa diem cua ca phong choi duoi redis
        roomRedisClusterHelper.removeRoomScore(reqCheckQuestionAnswer.getRoomId());

        // tao goi tin tra ve cho client
        Proto.ResCheckQuestionAnswer.Builder resCheckQuestionAnswer = Proto.ResCheckQuestionAnswer.newBuilder().setStatus(0).setQuestionId(reqCheckQuestionAnswer.getQuestionId());
        Proto.Packet packet = Proto.Packet.newBuilder().setResCheckQuestionAnswer(resCheckQuestionAnswer).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // gui thong tin ve cho client
        session.getAsyncRemote().sendObject(packetWrapper);


    }

    public void sendForAllSessionInRoom(Proto.PacketWrapper packetWrapper, int roomId) {
        // lay ra toan bo sessionId trong phong tu redis
        List<String> sessionList = roomRedisClusterHelper.getAllUserInRoom(roomId);
        // lay ra toan bo session dang online trong server nay
        Set<String> sessionListInServer = SessionCache.me().getKeys();

        sessionList.forEach(s -> {
            if (sessionListInServer.contains(s)) {
                Session session1 = SessionManage.me().get(s);
                // gui goi tin toi session
                session1.getAsyncRemote().sendObject(packetWrapper);
            } else {
                // publish xuong redis de cac tomcat khac biet
                roomNotify.publish(s, packetWrapper, roomId);
                System.out.println("publish to redis user has session : " + s);
            }
        });
    }

    public Proto.ResRoomScore getRoomScore(int roomid) {
        // lay ra diem cua cac nguoi choi trong phong
        Map<Integer, Integer> userScores = roomRedisClusterHelper.getRoomScore(roomid);
        Proto.ResRoomScore.Builder resRoomScore = Proto.ResRoomScore.newBuilder();
        if (userScores != null) {
            userScores.forEach((k, v) -> {
                Proto.UserScore.Builder userScore = Proto.UserScore.newBuilder().setUserId(k).setScore(v);
                resRoomScore.addUserScores(userScore);
            });
        } else {
            userScores = jdbi.onDemand(IExamAnswerDAO.class).getUserScoresBySessionId(roomRedisClusterHelper.getRoomContext(roomid).getExamSessionId());
            roomRedisClusterHelper.saveRoomScore(userScores, roomid);
            userScores.forEach((k, v) -> {
                Proto.UserScore.Builder userScore = Proto.UserScore.newBuilder().setUserId(k).setScore(v);
                resRoomScore.addUserScores(userScore);
            });
        }
        return resRoomScore.build();
    }

}
