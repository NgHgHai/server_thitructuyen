package edu.vn.hcmuaf.layer1.services;

import edu.vn.hcmuaf.layer2.convert.ExamConverter;
import edu.vn.hcmuaf.layer2.dao.*;
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
        ExamBean examBean = new ExamBean();
//        examBean = ExamConverter.convertCreateExamRequestToExamBean(request);
        examBean.setTitle(request.getTitle());
        examBean.setDescription(request.getDescription());
        examBean.setImageUrl(request.getImageUrl());
        examBean.setUserId(request.getUserId());
        examBean.setStatus(request.getStatus());
//        int examId = ExamDAO.insertExam(examBean);
        int examId = jdbi.onDemand(IExamDAO.class).insertExam(examBean);


        boolean success = examId != -1;
        String message = examId != -1 ? "Exam created successfully" : "Failed to create exam";
        System.out.println("ExamService : " + message + " with examId = " + examId);

        Proto.CreateExamResponse response = Proto.CreateExamResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setExamId(examId)
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setCreateExamResponse(response)
                .build());

        return response;
    }

    public Proto.UpdateExamResponse updateExam(Session session, Proto.UpdateExamRequest request) {
        if (!checkUserRequestIsHasPermission(session, request.getExamId())) {
            return null;
        }
        ExamBean examBean = new ExamBean();
//        examBean = ExamConverter.convertUpdateExamRequestToExamBean(request);
        examBean.setId(request.getExamId());
        examBean.setTitle(request.getTitle());
        examBean.setDescription(request.getDescription());
        examBean.setImageUrl(request.getImageUrl());
        examBean.setStatus(request.getStatus());

        boolean success = jdbi.onDemand(IExamDAO.class).updateExam(examBean) > 0;

        String message = success ? "Exam updated successfully" : "Failed to update exam";
        System.out.println("ExamService : " + message + " with examId = " + examBean.getId());
        Proto.UpdateExamResponse response = Proto.UpdateExamResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setUpdateExamResponse(response)
                .build());
        return response;
    }

    public Proto.GetExamResponse getExam(Session session, Proto.GetExamRequest getExamRequest) {
        if (!checkUserRequestIsHasPermission(session, getExamRequest.getExamId())) {
            return null;
        }
        ExamBean examBean = jdbi.onDemand(IExamDAO.class).getCompleteExamById(getExamRequest.getExamId(), jdbi);

        Proto.GetExamResponse response = ExamConverter.convertExamBeanToGetExamResponse(examBean, true);

        System.out.println("ExamService : Get exam with examId = " + getExamRequest.getExamId());

        sendResponse(session, Proto.Packet.newBuilder()
                .setGetExamResponse(response)
                .build());
        return response;
    }

    public boolean deleteExam(Session session, Proto.DeleteExamRequest deleteExamRequest) {
        try {
            if (!checkUserRequestIsHasPermission(session, deleteExamRequest.getExamId())) {
                return false;
            }
            boolean success = jdbi.onDemand(IExamDAO.class).deleteExam(deleteExamRequest.getExamId()) > 0;
            String message = success ? "Exam deleted successfully" : "Failed to delete exam";

            System.out.println("ExamService : " + message + " with examId = " + deleteExamRequest.getExamId());
            Proto.DeleteExamResponse response = Proto.DeleteExamResponse.newBuilder()
                    .setSuccess(success)
                    .setMessage(message)
                    .build();

            sendResponse(session, Proto.Packet.newBuilder()
                    .setDeleteExamResponse(response)
                    .build());
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean checkUserRequestIsHasPermission(Session session, int examId) {
        int UserIdFromExam = jdbi.onDemand(IExamDAO.class).getExamById(examId).getUserId();
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
        if (!checkUserRequestIsHasPermission(session, request.getExamId())) {
            return null;
        }
        QuestionBean questionBean = new QuestionBean();
        questionBean.setExamId(request.getExamId());
        questionBean.setQuestionText(request.getQuestionText());
        questionBean.setImageUrl(request.getImageUrl());
        questionBean.setTime(request.getTime());
        questionBean.setQuestionIndex(request.getQuestionIndex());

        int questionId = jdbi.onDemand(IQuestionDAO.class).insertQuestion(questionBean);
        for (int i = 1; i <= 4; i++) {
            ChoiceBean choiceBean = new ChoiceBean();
            choiceBean.setQuestionId(questionId);
            choiceBean.setChoiceIndex(i);
            jdbi.onDemand(IChoiceDAO.class).insertChoice(choiceBean);
        }
        boolean success = questionId != -1;
        String message = questionId != -1 ? "Question created successfully" : "Failed to create question";
        System.out.println("ExamService : " + message + " with questionId = " + questionId);

        Proto.CreateQuestionResponse response = Proto.CreateQuestionResponse.newBuilder()
                .setSuccess(success)
                .setQuestionId(questionId)
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setCreateQuestionResponse(response)
                .build());

        return response;
    }

    public Proto.UpdateQuestionResponse updateQuestion(Session session, Proto.UpdateQuestionRequest request) {
        if (!checkUserRequestIsHasPermission(session, request.getExamId())) {
            return null;
        }
        QuestionBean questionBean = new QuestionBean();
        questionBean.setId(request.getQuestionId());
        questionBean.setExamId(request.getExamId());
        questionBean.setQuestionText(request.getQuestionText());
        questionBean.setImageUrl(request.getImageUrl());
        questionBean.setTime(request.getTime());
        questionBean.setQuestionIndex(request.getQuestionIndex());

        boolean success = jdbi.onDemand(IQuestionDAO.class).updateQuestion(questionBean) > 0;
        List<ChoiceBean> choiceBeans = request.getChoicesList().stream()
                .map(choice -> {
                    ChoiceBean choiceBean = new ChoiceBean();
                    choiceBean.setQuestionId(questionBean.getId());
                    choiceBean.setChoiceIndex(choiceBean.getChoiceIndex());
                    choiceBean.setChoiceText(choice.getChoiceText());
                    choiceBean.setImageUrl(choice.getImageUrl());
                    choiceBean.setCorrect(choice.getIsCorrect());
                    return choiceBean;
                })
                .collect(Collectors.toList());
        for (ChoiceBean choiceBean : choiceBeans) {
            jdbi.onDemand(IChoiceDAO.class).updateChoice(choiceBean);
        }
        String message = success ? "Question updated successfully" : "Failed to update question";
        System.out.println("ExamService : " + message + " with questionId = " + questionBean.getId());
        Proto.UpdateQuestionResponse response = Proto.UpdateQuestionResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setUpdateQuestionResponse(response)
                .build());
        return response;
    }

    public void deleteQuestion(Session session, Proto.DeleteQuestionRequest deleteQuestionRequest) {
        if (!checkUserRequestIsHasPermission(session, deleteQuestionRequest.getExamId())) {
            return;
        }
        boolean success = jdbi.onDemand(IQuestionDAO.class).deleteQuestion(deleteQuestionRequest.getQuestionId()) > 0;
        for (int i = 1; i <= 4; i++) {
            jdbi.onDemand(IChoiceDAO.class).deleteChoice(deleteQuestionRequest.getQuestionId());
        }
        String message = success ? "Question deleted successfully" : "Failed to delete question";

        System.out.println("ExamService : " + message + " with questionId = " + deleteQuestionRequest.getQuestionId());
        Proto.DeleteQuestionResponse response = Proto.DeleteQuestionResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setDeleteQuestionResponse(response)
                .build());
    }

    private void sendResponse(Session session, Proto.Packet packet) {
        Proto.PacketWrapper packets = Proto.PacketWrapper.newBuilder().addPacket(packet).build();
        if (session != null && session.isOpen())
            session.getAsyncRemote().sendObject(packets);
    }


    public void getAllExamByUserId(Session session, Proto.GetAllExamRequest getAllExamRequest) {
        String sessionId = SessionManage.me().getSessionID(session);
        SessionCache sessionCache = SessionCache.me();
        int userId = sessionCache.get(sessionId).getUser().getUserId();
        List<ExamBean> exams = jdbi.onDemand(IExamDAO.class).getAllExamsByUserId(userId);
        Proto.GetAllExamResponse response = Proto.GetAllExamResponse.newBuilder()
                .addAllExams(exams.stream()
                        .map(examBean -> ExamConverter.convertExamBeanToGetExamResponse(examBean, false))
                        .collect(Collectors.toList()))
                .build();

        sendResponse(session, Proto.Packet.newBuilder()
                .setGetAllExamResponse(response)
                .build());
    }

}

