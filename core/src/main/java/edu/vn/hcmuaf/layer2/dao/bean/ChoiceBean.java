package edu.vn.hcmuaf.layer2.dao.bean;

import edu.vn.hcmuaf.layer2.proto.Proto;
import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ChoiceBean {
    @ColumnName("id")
    private int id;
    @ColumnName("question_id")
    private int questionId;
    @ColumnName("choice_index")
    private int choiceIndex;
    @ColumnName("choice_text")
    private String choiceText;
    @ColumnName("image_url")
    private String imageUrl;
    @ColumnName("is_correct")
    private boolean isCorrect;
    @ColumnName("status")
    private int status;
    @ColumnName("created_at")
    private Timestamp createdAt;
    @ColumnName("updated_at")
    private Timestamp updatedAt;

    public Proto.ChoiceResponse getChoiceResponse() {
        Proto.ChoiceResponse.Builder builder = Proto.ChoiceResponse.newBuilder();
        builder.setChoiceId(id);
        builder.setChoiceIndex(choiceIndex);
        builder.setChoiceText(choiceText);
        builder.setImageUrl(imageUrl);
        return builder.build();
    }
}
