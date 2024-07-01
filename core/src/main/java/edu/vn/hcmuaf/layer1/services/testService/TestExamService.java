package edu.vn.hcmuaf.layer1.services.testService;

import edu.vn.hcmuaf.layer1.services.ExamService;
import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import jakarta.websocket.Session;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestExamService {

    private ExamService examService = new ExamService();

    public void testInsertExamForUser(int userId) {

        ExamBean exam = createExamBean(userId);
        Proto.CreateExamRequest request = createExamRequest(userId);
        Session session = null;
//        examService.createExam(session, request);

        examService.createExam(session,request);


//        ExamBean insertedExam = examDAO.findExamById(exam.getId());
//        if (insertedExam != null) {
//            System.out.println("Exam inserted successfully:");
//            System.out.println(insertedExam);
//        } else {
//            System.out.println("Failed to insert exam.");
//        }
    }

    private Proto.CreateExamRequest createExamRequest(int userId) {
        Proto.CreateExamRequest.Builder requestBuilder = Proto.CreateExamRequest.newBuilder();
        requestBuilder.setUserId(userId);
        requestBuilder.setTitle("Sample Exam");
        requestBuilder.setDescription("This is a sample exam");


        Proto.Question.Builder questionBuilder = Proto.Question.newBuilder();
        questionBuilder.setQuestionText("What is the capital of France?");
        questionBuilder.setImageUrl("http://example.com/image1.jpg");

        Proto.Choice.Builder choiceBuilder = Proto.Choice.newBuilder();
        choiceBuilder.setChoiceText("Paris");
        choiceBuilder.setIsCorrect(true);
        questionBuilder.addChoices(choiceBuilder.build());

        choiceBuilder.setChoiceText("London");
        choiceBuilder.setIsCorrect(false);
        questionBuilder.addChoices(choiceBuilder.build());

        choiceBuilder.setChoiceText("Berlin");
        choiceBuilder.setIsCorrect(false);
        questionBuilder.addChoices(choiceBuilder.build());

        choiceBuilder.setChoiceText("Madrid");
        choiceBuilder.setIsCorrect(false);
        questionBuilder.addChoices(choiceBuilder.build());

        requestBuilder.addQuestions(questionBuilder.build());

        return requestBuilder.build();
    }

    private ExamBean createExamBean(int userId) {
        ExamBean exam = new ExamBean();
        exam.setId(15);
        exam.setTitle("Sample Exammmmmmmmmmmm");
        exam.setDescription("This is a sample exam");
        exam.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        exam.setUserId(userId); // Assume user with id 1

        List<QuestionBean> questions = new ArrayList<>();


        QuestionBean question1 = new QuestionBean();
//        question1.setExamId(exam.getId());
//        question1.setQuestionOrder(1);
        question1.setQuestionText("What is the capitalllllllllll of France?");
        question1.setImageUrl("http://example.com/image1.jpg");



        List<ChoiceBean> choices1 = new ArrayList<>();
        choices1.add(ChoiceBean.builder().choiceText("Parisssssss").isCorrect(true).build());
        choices1.add(ChoiceBean.builder().choiceText("London").isCorrect(false).build());
        choices1.add(ChoiceBean.builder().choiceText("Berlin").isCorrect(false).build());
        choices1.add(ChoiceBean.builder().choiceText("Madrid").isCorrect(false).build());



        question1.setChoices(choices1);
        questions.add(question1);




        exam.setQuestions(questions);

        return exam;
    }

    public static void main(String[] args) {
        TestExamService testService = new TestExamService();
        testService.testInsertExamForUser(2);
//        System.out.println(testService.createExamBean(2));
    }
}
