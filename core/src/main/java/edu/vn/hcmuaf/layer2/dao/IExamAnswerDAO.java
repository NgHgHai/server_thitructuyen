package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ExamAnswerBean;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;
import java.util.Map;

public interface IExamAnswerDAO {

    @SqlUpdate("INSERT INTO exam_answers (exam_session_id, question_id, choice_id, user_id, answer_time) VALUES (:examSessionId, :questionId, :choiceId, :userId, :answerTime)")
    @GetGeneratedKeys
    int insertExamAnswer(@BindBean ExamAnswerBean examAnswer);

    @SqlQuery("SELECT * FROM exam_answers WHERE id = :id")
    @RegisterBeanMapper(ExamAnswerBean.class)
    ExamAnswerBean getExamAnswerById(@Bind("id") int id);

    @SqlUpdate("UPDATE exam_answers SET exam_session_id = :examSessionId, question_id = :questionId, choice_id = :choiceId, user_id = :userId, answer_time = :answerTime WHERE id = :id")
    void updateExamAnswer(@BindBean ExamAnswerBean examAnswer);

    @SqlUpdate("UPDATE exam_answers SET status = -1 WHERE id = :id")
    void deleteExamAnswer(@Bind("id") int id);

    @SqlQuery("SELECT * FROM exam_answers WHERE exam_session_id = :examSessionId")
    @RegisterBeanMapper(ExamAnswerBean.class)
    List<ExamAnswerBean> getExamAnswersBySessionId(@Bind("examSessionId") int examSessionId);

    @SqlQuery("SELECT user_id, COUNT(*) AS correct_answers " +
            "FROM exam_answers ea " +
            "JOIN choices c ON ea.choice_id = c.id " +
            "WHERE ea.exam_session_id = :examSessionId AND c.is_correct = true " +
            "GROUP BY user_id")
    Map<Integer, Integer> getUserScoresBySessionId(@Bind("examSessionId") int examSessionId);
}
