package edu.vn.hcmuaf.layer2.dao.bean;

import edu.vn.hcmuaf.layer2.proto.Proto;
import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class QuestionBean {
    @ColumnName("id")
    private int id;
    @ColumnName("exam_id")
    private int examId;
    @ColumnName("question_index")
    private int questionIndex;
    @ColumnName("question_text")
    private String questionText;
    @ColumnName("image_url")
    private String imageUrl;
    @ColumnName("time")
    private int time; // thoi gian tra loi cau hoi
    @ColumnName("status")
    private int status;
    @ColumnName("created_at")
    private Timestamp createdAt;
    @ColumnName("updated_at")
    private Timestamp updatedAt;
    private List<ChoiceBean> choices;
    public QuestionBean(Proto.Question question) {
        this.id = question.getQuestionId();
        this.examId = question.getExamId();
        this.questionIndex = question.getQuestionIndex();
        this.questionText = question.getQuestionText();
        this.imageUrl = question.getImageUrl();
        this.time = question.getTime();
        this.status = question.getStatus();
    }
    public Proto.Question getProtoQuestion(boolean includeRightAnswer) {
        Proto.Question.Builder builder = Proto.Question.newBuilder();
        builder.setQuestionId(id);
        builder.setExamId(examId);
        builder.setQuestionIndex(questionIndex);
        builder.setQuestionText(questionText);
        builder.setImageUrl(imageUrl);
        builder.setTime(time);
        builder.setStatus(status);
        builder.setQuestionIndex(questionIndex);
        for (ChoiceBean choice : choices) {
            builder.addChoices(choice.getProtoChoice(includeRightAnswer));
        }
        return builder.build();
    }
}
