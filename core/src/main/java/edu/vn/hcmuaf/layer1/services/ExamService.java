package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.convert.ExamConverter;
import edu.vn.hcmuaf.layer2.dao.ChoiceDAO;
import edu.vn.hcmuaf.layer2.dao.ExamDAO;
import edu.vn.hcmuaf.layer2.dao.PoolConnectDAO;
import edu.vn.hcmuaf.layer2.dao.QuestionDAO;
import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import edu.vn.hcmuaf.layer2.redis.SessionManage;
import edu.vn.hcmuaf.layer2.redis.cache.SessionCache;
import jakarta.websocket.Session;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.stream.Collectors;

public class ExamService extends PoolConnectDAO {
    public static final ExamService instance = new ExamService();

    public static ExamService me() {
        return instance;
    }

    public Jdbi jdbi = getJdbi();

    public Proto.CreateExamResponse createExam(Session session, Proto.CreateExamRequest request) {
        ExamBean examBean = new ExamBean(request.getExam());
        int examId = ExamDAO.insertExam(examBean);
        QuestionBean questionBean = QuestionBean.builder().examId(examId).questionIndex(1).build();
        int firstQuestionId = QuestionDAO.insertQuestion(QuestionBean.builder().examId(examId).questionIndex(1).build());
        for (int i = 1; i <= 4; i++) {
            ChoiceBean choiceBean = new ChoiceBean();
            choiceBean.setQuestionId(firstQuestionId);
            choiceBean.setChoiceIndex(i);
            ChoiceDAO.insertChoice(choiceBean);
        }
        boolean success = examId != -1;
        String message = examId != -1 ? "Exam created successfully" : "Failed to create exam";
        System.out.println("ExamService : " + message + " with examId = " + examId);

        Proto.CreateExamResponse response = Proto.CreateExamResponse.newBuilder().setSuccess(success).setMessage(message).setExamId(examId).setFirstQuestionId(firstQuestionId).build();

        sendResponse(session, Proto.Packet.newBuilder().setCreateExamResponse(response).build());

        return response;
    }

    public Proto.UpdateExamResponse updateExam(Session session, Proto.UpdateExamRequest request) {
//        if (!checkUserRequestIsHasPermission(session, request.getExam().getExamId())) {
//            return null;
//        }
        ExamBean examBean = new ExamBean(request.getExam());

        boolean success = ExamDAO.updateExam(examBean) > 0;

        String message = success ? "Exam updated successfully" : "Failed to update exam";
        System.out.println("ExamService : " + message + " with examId = " + examBean.getId());
        Proto.UpdateExamResponse response = Proto.UpdateExamResponse.newBuilder().setSuccess(success).setMessage(message).build();

        sendResponse(session, Proto.Packet.newBuilder().setUpdateExamResponse(response).build());
        return response;
    }

    public Proto.GetExamResponse getExam(Session session, Proto.GetExamRequest getExamRequest) {
//        if (!checkUserRequestIsHasPermission(session, getExamRequest.getExamId())) {
//            return null;
//        }
        ExamBean examBean = ExamDAO.getCompleteExamById(getExamRequest.getExamId());

        Proto.Exam protoExam = ExamConverter.convertExamBeanToProtoExam(examBean, true);
        Proto.GetExamResponse response = Proto.GetExamResponse.newBuilder().setExam(protoExam).build();

        System.out.println("ExamService : Get exam with examId = " + protoExam.getExamId());

        sendResponse(session, Proto.Packet.newBuilder().setGetExamResponse(response).build());
        return response;
    }

    public boolean deleteExam(Session session, Proto.DeleteExamRequest deleteExamRequest) {
        try {
            if (!checkUserRequestIsHasPermission(session, deleteExamRequest.getExamId())) {
                return false;
            }
            boolean success = ExamDAO.deleteExam(deleteExamRequest.getExamId()) > 0;
            String message = success ? "Exam deleted successfully" : "Failed to delete exam";

            System.out.println("ExamService : " + message + " with examId = " + deleteExamRequest.getExamId());
            Proto.DeleteExamResponse response = Proto.DeleteExamResponse.newBuilder().setSuccess(success).setMessage(message).build();

            sendResponse(session, Proto.Packet.newBuilder().setDeleteExamResponse(response).build());
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean checkUserRequestIsHasPermission(Session session, int examId) {
        int UserIdFromExam = ExamDAO.getExamById(examId).getUserId();
        String sessionId = SessionManage.me().getSessionID(session);
        SessionCache sessionCache = SessionCache.me();
        int userId = sessionCache.get(sessionId).getUser().getUserId();
        if (userId != UserIdFromExam) {
            System.out.println("ExamService : User request is not owner of exam");
            return false;
        }

        return true;
    }

    public Proto.CreateQuestionResponse createQuestion(Session session, Proto.CreateQuestionRequest request) {
//        if (!checkUserRequestIsHasPermission(session, request.getExamId())) {
//            return null;
//        }
        QuestionBean questionBean = new QuestionBean();
        questionBean.setExamId(request.getExamId());
//        questionBean.setTime(0);

        int questionId = QuestionDAO.insertQuestion(questionBean);
        for (int i = 1; i <= 4; i++) {
            ChoiceBean choiceBean = new ChoiceBean();
            choiceBean.setQuestionId(questionId);
            choiceBean.setChoiceIndex(i);
            System.out.println("dang o exam service, tu tao choice voi id = " + choiceBean.getQuestionId() + " va index = " + choiceBean.getChoiceIndex());
            ChoiceDAO.insertChoice(choiceBean);
        }
        boolean success = questionId != -1;
        String message = questionId != -1 ? "Question created successfully" : "Failed to create question";
        System.out.println("ExamService : " + message + " with questionId = " + questionId);

        Proto.CreateQuestionResponse response = Proto.CreateQuestionResponse.newBuilder().setSuccess(success).setQuestionId(questionId).build();

        sendResponse(session, Proto.Packet.newBuilder().setCreateQuestionResponse(response).build());

        return response;
    }

    public Proto.UpdateQuestionResponse updateQuestion(Session session, Proto.UpdateQuestionRequest request) {
//        if (!checkUserRequestIsHasPermission(session, request.getQuestion().getExamId())) {
//            return null;
//        }
        Proto.Question question = request.getQuestion();
        QuestionBean questionBean = new QuestionBean(question);
        System.out.println(question);

        boolean success = QuestionDAO.updateQuestion(questionBean) > 0;
        List<Proto.Choice> choices = question.getChoicesList();
        for (Proto.Choice choice : choices) {
            ChoiceBean choiceBean = new ChoiceBean(choice);
            ChoiceDAO.updateChoice(choiceBean);
        }
        String message = success ? "Question updated successfully" : "Failed to update question";
        System.out.println("ExamService : " + message + " with questionId = " + questionBean.getId() + "question_index = " + questionBean.getQuestionIndex());
        Proto.UpdateQuestionResponse response = Proto.UpdateQuestionResponse.newBuilder().setQuestion(question).setSuccess(success).setMessage(message).build();

        sendResponse(session, Proto.Packet.newBuilder().setUpdateQuestionResponse(response).build());
        return response;
    }

    public void deleteQuestion(Session session, Proto.DeleteQuestionRequest deleteQuestionRequest) {
//        int examId = QuestionDAO.getQuestionById(deleteQuestionRequest.getQuestionId()).getExamId();
//        if (!checkUserRequestIsHasPermission(session, )) {
//            return;
//        }
        boolean success = QuestionDAO.deleteQuestion(deleteQuestionRequest.getQuestionId()) > 0;
        for (int i = 1; i <= 4; i++) {
            ChoiceDAO.deleteChoice(deleteQuestionRequest.getQuestionId());
        }
        String message = success ? "Question deleted successfully" : "Failed to delete question";

        System.out.println("ExamService : " + message + " with questionId = " + deleteQuestionRequest.getQuestionId());
        Proto.DeleteQuestionResponse response = Proto.DeleteQuestionResponse.newBuilder().setQuestionId(deleteQuestionRequest.getQuestionId()).setSuccess(success).setMessage(message).build();

        sendResponse(session, Proto.Packet.newBuilder().setDeleteQuestionResponse(response).build());
    }

    public void getQuestion(Session session, Proto.GetQuestionRequest getQuestionRequest) {
        QuestionBean questionBean = QuestionDAO.getCompleteQuestionById(getQuestionRequest.getQuestionId());
        Proto.Question protoQuestion = ExamConverter.convertQuestionBeanToQuestion(questionBean, true);
        Proto.GetQuestionResponse response = Proto.GetQuestionResponse.newBuilder().setQuestion(protoQuestion).build();
        System.out.println("ExamService : Get question with questionId = " + protoQuestion.getQuestionId());
        sendResponse(session, Proto.Packet.newBuilder().setGetQuestionResponse(response).build());
    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen()) session.getAsyncRemote().sendObject(packets);
    }


    public void getAllExamByUserId(Session session, Proto.GetAllExamRequest getAllExamRequest) {
        String sessionId = SessionManage.me().getSessionID(session);
        SessionCache sessionCache = SessionCache.me();
        int userId = sessionCache.get(sessionId).getUser().getUserId();
        List<ExamBean> exams = ExamDAO.getAllExamsByUserId(userId);
        Proto.GetAllExamResponse response = Proto.GetAllExamResponse.newBuilder().addAllExam(exams.stream().map(examBean -> ExamConverter.convertExamBeanToProtoExam(examBean, false)).collect(Collectors.toList())).build();

        sendResponse(session, Proto.Packet.newBuilder().setGetAllExamResponse(response).build());
    }
//    public void getExamByExamId(Session session, Proto.GetExamRequest request) {
//        ExamBean examBean = ExamDAO.getCompleteExamById(request.getExamId());
//        Proto.Exam protoExam = ExamConverter.convertExamBeanToProtoExam(examBean, true);
//        Proto.GetExamResponse response = Proto.GetExamResponse.newBuilder()
//                .setExam(protoExam)
//                .build();
//
//        sendResponse(session, Proto.Packet.newBuilder()
//                .setGetExamResponse(response)
//                .build());
//    }

}

