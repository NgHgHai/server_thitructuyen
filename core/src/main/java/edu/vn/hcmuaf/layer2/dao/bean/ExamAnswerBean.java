package edu.vn.hcmuaf.layer2.dao.bean;

import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ExamAnswerBean {
    @ColumnName("id")
    private int id;
    @ColumnName("exam_session_id")
    private int examSessionId;
    @ColumnName("question_id")
    private int questionId;
    @ColumnName("choice_id")
    private int choiceId;
    @ColumnName("user_id")
    private int userId;
    @ColumnName("answer_time")
    private Timestamp answerTime;
}
