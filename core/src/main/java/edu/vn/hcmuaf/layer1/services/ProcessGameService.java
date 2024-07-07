package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.dao.ExamAnswerDAO;
import edu.vn.hcmuaf.layer2.dao.ExamSessionDAO;
import edu.vn.hcmuaf.layer2.dao.PoolConnectDAO;
import edu.vn.hcmuaf.layer2.dao.QuestionDAO;
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

        int examSessionId = ExamSessionDAO.insertExamSession(examSessionBean);
        // lay ra roomContext tu redis
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(reqStartExam.getRoomId());
        // set cac thong tin cua roomContext
        roomContext.setExamSessionId(examSessionId);

        roomContext.setHostId(reqStartExam.getHostId());//lay tam, dang le phai lay tu sessioncontext
        roomContext.setTestId(reqStartExam.getExamId());//lay tam

        roomContext.setStatus(1);
//        List<QuestionBean> questionBeans = QuestionDAO.getQuestionsByExamId(reqStartExam.getExamId());
        // lay ra cau hoi dau tien
        QuestionBean firstQuestion = QuestionDAO.getCompleteQuestionByIndexAndExamId(reqStartExam.getExamId(), 1);
        // cap nhat thong tin ve cau hoi hien tai cua roomContext cho redis
        roomContext.setCurrentQuestionId(firstQuestion.getId());
        roomContext.setCurrentquestionIndex(firstQuestion.getQuestionIndex());
        // luu lai roomContext vao redis
        roomRedisClusterHelper.addRoomContext(roomContext.getRoomId(), roomContext);

        // tao goi tin tra ve cho client
        Proto.Question question = firstQuestion.getProtoQuestion(false);
        Proto.ResStartExam resStartExam = Proto.ResStartExam.newBuilder().setExamSessionId(examSessionId).setQuestion(question).build();
        Proto.Packet packetResExam = Proto.Packet.newBuilder().setResStartExam(resStartExam).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packetResExam).build();


        // gui thong tin ve cho clients
        sendForAllSessionInRoom(packetWrapper, reqStartExam.getRoomId());
        System.out.println("start exam success");

    }

    public void nextQuestion(Session session, Proto.ReqGetNextQuestion reqGetNextQuestion) {
        // lay ra roomContext tu redis
        RoomContext roomContext = roomRedisClusterHelper.getRoomContext(reqGetNextQuestion.getRoomId());
        // lay ra id cua bai thi
        int examId = roomContext.getTestId();
        // lay ra so luong cau hoi cua bai thi
        int totalQuestion = QuestionDAO.getTotalQuestionByExamId(examId);

        // lay ra cau hoi hien tai
        int currentQuestionIndex = roomContext.getCurrentquestionIndex();
        // lay ra cau hoi tiep theo
        QuestionBean nextQuestion = QuestionDAO.getCompleteQuestionByIndexAndExamId(examId, currentQuestionIndex + 1);

        // neu khong co cau hoi tiep theo thi ket thuc bai thi
        if (nextQuestion == null) {
            endExam(session, Proto.ReqEndExam.newBuilder().setRoomId(reqGetNextQuestion.getRoomId()).build());
            return;
        }

        // cap nhat thong tin ve cau hoi hien tai cua roomContext cho redis
        roomContext.setCurrentQuestionId(nextQuestion.getId());
        roomContext.setCurrentquestionIndex(nextQuestion.getQuestionIndex());
        // luu lai roomContext vao redis
        roomRedisClusterHelper.addRoomContext(roomContext.getRoomId(), roomContext);

        // tao goi tin tra ve cho client
        Proto.Question question = nextQuestion.getProtoQuestion(true);
        Proto.RoomScore resRoomScore = getRoomScore(reqGetNextQuestion.getRoomId());

        Proto.ResGetNextQuestion resGetNextQuestion = Proto.ResGetNextQuestion.newBuilder().setQuestion(question).setTotalQuestion(totalQuestion).build();
        Proto.Packet packet = Proto.Packet.newBuilder().setResGetNextQuestion(resGetNextQuestion).build();
        Proto.Packet packetRoomScore = Proto.Packet.newBuilder().setRoomScore(resRoomScore).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).addPacket(packetRoomScore).build();
        System.out.println("next question success");
        System.out.println(packetWrapper);
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
        // them vao exam_session end time
        ExamSessionBean examSessionBean = ExamSessionDAO.getExamSessionById(roomContext.getExamSessionId());
        examSessionBean.setEndTime(new java.sql.Timestamp(System.currentTimeMillis()));
        ExamSessionDAO.updateExamSession(examSessionBean);


        // tao goi tin tra ve cho client
        Proto.ResEndExam.Builder resEndExam = Proto.ResEndExam.newBuilder().setResRoomScore(getRoomScore(reqEndExam.getRoomId()));
        Proto.Packet packet = Proto.Packet.newBuilder().setResEndExam(resEndExam).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // gui thong tin ve cho client
        sendForAllSessionInRoom(packetWrapper, reqEndExam.getRoomId());
    }


    public void checkQuestionAnswer(Session session, Proto.ReqCheckQuestionAnswer reqCheckQuestionAnswer) {
        System.out.println("check question answer");
        System.out.println(reqCheckQuestionAnswer);
        // neu user nao da tra loi cau hoi nay roi thi khong cho tra loi nua
        if (ExamAnswerDAO.checkHasAnswerBySessionIdAndUserIdAndQuestionId(reqCheckQuestionAnswer.getExamSessionId(), reqCheckQuestionAnswer.getQuestionId(), reqCheckQuestionAnswer.getUserId())) {
            System.out.println("user has answered this question");
            Proto.ResCheckQuestionAnswer.Builder resCheckQuestionAnswer = Proto.ResCheckQuestionAnswer.newBuilder().setStatus(400).setQuestionId(reqCheckQuestionAnswer.getQuestionId());
            Proto.Packet packet = Proto.Packet.newBuilder().setResCheckQuestionAnswer(resCheckQuestionAnswer).build();
            Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
            return;
        }
        // luu cau tra loi cua nguoi choi vao db
        ExamAnswerBean examAnswerBean = new ExamAnswerBean();
        examAnswerBean.setExamSessionId(reqCheckQuestionAnswer.getExamSessionId());
        examAnswerBean.setQuestionId(reqCheckQuestionAnswer.getQuestionId());
        examAnswerBean.setChoiceId(reqCheckQuestionAnswer.getChoiceId());
        examAnswerBean.setUserId(reqCheckQuestionAnswer.getUserId());
        ExamAnswerDAO.insertExamAnswer(examAnswerBean);

        //check xem cau tra loi dung hay sai
        QuestionBean questionBean = QuestionDAO.getCompleteQuestionById(reqCheckQuestionAnswer.getQuestionId());
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
        Proto.ResCheckQuestionAnswer.Builder resCheckQuestionAnswer = Proto.ResCheckQuestionAnswer.newBuilder().setStatus(isCorrect ? 1 : 0).setQuestionId(reqCheckQuestionAnswer.getQuestionId());
        Proto.Packet packet = Proto.Packet.newBuilder().setResCheckQuestionAnswer(resCheckQuestionAnswer).build();
        Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(packet).build();

        // gui thong tin ve cho client
        session.getAsyncRemote().sendObject(packetWrapper);
        System.out.println(reqCheckQuestionAnswer);
        System.out.println("check question answer success");
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

    public Proto.RoomScore getRoomScore(int roomid) {
        // lay ra diem cua cac nguoi choi trong phong
        Map<String, Integer> userScores = roomRedisClusterHelper.getRoomScore(roomid);
        Proto.RoomScore.Builder roomScore = Proto.RoomScore.newBuilder();
        if (userScores != null) {
            userScores.forEach((k, v) -> {
                Proto.UserScore.Builder userScore = Proto.UserScore.newBuilder().setUserName(k).setScore(v);
                roomScore.addUserScores(userScore);
            });
        } else {
            userScores = ExamAnswerDAO.getUserScoresBySessionId(roomRedisClusterHelper.getRoomContext(roomid).getExamSessionId());
            roomRedisClusterHelper.saveRoomScore(userScores, roomid);
            userScores.forEach((k, v) -> {
                Proto.UserScore.Builder userScore = Proto.UserScore.newBuilder().setUserName(k).setScore(v);
                roomScore.addUserScores(userScore);
            });
        }
        return roomScore.build();
    }
}
