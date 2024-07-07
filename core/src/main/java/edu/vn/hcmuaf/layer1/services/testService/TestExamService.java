package edu.vn.hcmuaf.layer1.services.testService;

import edu.vn.hcmuaf.layer1.services.ExamService;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.List;

public class TestExamService {

    private ExamService examService = new ExamService();

    public void testInsertExamForUser(int userId) {

        Proto.CreateExamRequest request = Proto.CreateExamRequest.newBuilder()
                .setExam(Proto.Exam.newBuilder()
                        .setTitle("Test Exam")
                        .setDescription("Test Exam Description")
                        .setImageUrl("Test Image URL")
                        .setUserId(userId)
                        .build())
                .build();

        Session session = null;
        Proto.CreateExamResponse createExamResponse = examService.createExam(session, request);
        int examId = createExamResponse.getExamId();
        System.out.println("Exam ID: " + examId);

        Proto.CreateQuestionRequest questionRequest = Proto.CreateQuestionRequest.newBuilder()
//                .setQuestion(createQuestion(examId))
                .setExamId(examId)
                .build();
        Proto.CreateQuestionResponse response = examService.createQuestion(session, questionRequest);
        int questionId = response.getQuestionId();
        Proto.Question question = createQuestion(questionId, examId);
        Proto.UpdateQuestionResponse updateQuestionResponse =
                examService.updateQuestion(session, Proto.UpdateQuestionRequest.newBuilder()
                        .setQuestion(question)
                        .build());
        System.out.println(updateQuestionResponse);
        createQuestion(1, 1);
    }

    private Proto.Question createQuestion(int questionId, int examId) {
        List<Proto.Choice> choices = new ArrayList<>();
        choices.add(Proto.Choice.newBuilder()
                .setChoiceIndex(1)
                .setChoiceText("Choice 1")
                .setIsCorrect(true)
                .setQuestionId(questionId)
                .build());
        choices.add(Proto.Choice.newBuilder()
                .setChoiceIndex(2)
                .setChoiceText("Choice 2")
                .setIsCorrect(false)
                .setQuestionId(questionId)
                .build());
        choices.add(Proto.Choice.newBuilder()
                .setChoiceIndex(3)
                .setChoiceText("Choice 3")
                .setIsCorrect(false)
                .setQuestionId(questionId)
                .build());
        choices.add(Proto.Choice.newBuilder()
                .setChoiceIndex(4)
                .setChoiceText("Choice 4")
                .setIsCorrect(false)
                .setQuestionId(questionId)
                .build());
        System.out.println(choices);
        return Proto.Question.newBuilder()
                .setExamId(examId)
                .setQuestionId(questionId)
                .setQuestionIndex(1)
                .setQuestionText("Question 1")
                .addAllChoices(choices)
                .build();
    }


    public static void main(String[] args) {
        TestExamService testService = new TestExamService();
        testService.testInsertExamForUser(2);
//        System.out.println(testService.createExamBean(2));
    }
}
