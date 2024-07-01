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

    public Proto.QuestionResponse getQuestionResponse() {
        Proto.QuestionResponse.Builder builder = Proto.QuestionResponse.newBuilder();
        builder.setQuestionId(id);
        builder.setQuestionIndex(questionIndex);
        builder.setQuestionText(questionText);
        builder.setImageUrl(imageUrl);
        builder.setTime(time);
        builder.setQuestionIndex(questionIndex);
        for (ChoiceBean choice : choices) {
            builder.addChoices(choice.getChoiceResponse());
        }
        return builder.build();
    }
}
