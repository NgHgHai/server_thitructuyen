package edu.vn.hcmuaf.layer2.convert;

import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import edu.vn.hcmuaf.layer2.proto.Proto;

import java.util.List;
import java.util.stream.Collectors;

public class ExamConverter {

    public static Proto.Exam convertExamBeanToProtoExam(ExamBean examBean, boolean includeQuestions) {

        Proto.Exam.Builder responseBuilder = Proto.Exam.newBuilder();

        responseBuilder.setExamId(examBean.getId());
        responseBuilder.setTitle(examBean.getTitle() + "");
        responseBuilder.setDescription(examBean.getDescription() + "");
        responseBuilder.setImageUrl(examBean.getImageUrl() + "");
        responseBuilder.setCreatedAt(String.valueOf(examBean.getCreatedAt()));
        responseBuilder.setStatus(examBean.getStatus());

        if (includeQuestions)
            if (examBean.getQuestions() != null) {
                responseBuilder.addAllQuestions(
                        examBean.getQuestions().stream()
                                .map(questionBean -> convertQuestionBeanToQuestion(questionBean, includeQuestions))
                                .collect(Collectors.toList())
                );
            }

        return responseBuilder.build();
    }


    public static Proto.Question convertQuestionBeanToQuestion(QuestionBean questionBean, boolean includeRightAnswer) {

        Proto.Question.Builder questionBuilder = Proto.Question.newBuilder();

        questionBuilder.setQuestionId(questionBean.getId());
        questionBuilder.setQuestionIndex(questionBean.getQuestionIndex());
        questionBuilder.setQuestionText(questionBean.getQuestionText() + "");
        questionBuilder.setImageUrl(questionBean.getImageUrl() + "");


        if (questionBean.getChoices() != null) {
            questionBuilder.addAllChoices(
                    questionBean.getChoices().stream()
                            .map(choiceBean -> convertChoiceBeanToChoice(choiceBean, includeRightAnswer))
                            .collect(Collectors.toList())
            );
        }
        return questionBuilder.build();
    }


    public static Proto.Choice convertChoiceBeanToChoice(ChoiceBean choiceBean, boolean includeCorrectness) {

        Proto.Choice.Builder choiceBuilder = Proto.Choice.newBuilder();

        choiceBuilder.setChoiceId(choiceBean.getId());
        choiceBuilder.setChoiceIndex(choiceBean.getChoiceIndex());
        choiceBuilder.setChoiceText(choiceBean.getChoiceText() + "");
        choiceBuilder.setImageUrl(choiceBean.getImageUrl() + "");
        if (includeCorrectness)
            choiceBuilder.setIsCorrect(choiceBean.isCorrect());
        return choiceBuilder.build();
    }

}
