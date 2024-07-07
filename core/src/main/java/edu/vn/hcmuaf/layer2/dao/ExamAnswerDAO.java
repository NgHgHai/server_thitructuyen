package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ExamAnswerBean;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamAnswerDAO extends PoolConnectDAO {
    private static Jdbi jdbi = getJdbi();

    public static int insertExamAnswer(ExamAnswerBean examAnswer) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("INSERT INTO exam_answers (exam_session_id, question_id, choice_id, user_id, answer_time) VALUES (:examSessionId, :questionId, :choiceId, :userId, :answerTime)")
                    .bindBean(examAnswer);
            return update.executeAndReturnGeneratedKeys("id").mapTo(int.class).findOnly();
        });
    }

    public static ExamAnswerBean getExamAnswerById(int id) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_answers WHERE id = :id")
                    .bind("id", id);
            return query.mapToBean(ExamAnswerBean.class).findFirst().orElse(null);
        });
    }

    public static void updateExamAnswer(ExamAnswerBean examAnswer) {
        jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exam_answers SET exam_session_id = :examSessionId, question_id = :questionId, choice_id = :choiceId, user_id = :userId, answer_time = :answerTime WHERE id = :id")
                    .bindBean(examAnswer);
            return update.execute();
        });
    }

    public static void deleteExamAnswer(int id) {
        jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exam_answers SET status = -1 WHERE id = :id")
                    .bind("id", id);
            return update.execute();
        });
    }

    public static List<ExamAnswerBean> getExamAnswersBySessionId(int examSessionId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_answers WHERE exam_session_id = :examSessionId")
                    .bind("examSessionId", examSessionId);
            return query.mapToBean(ExamAnswerBean.class).list();
        });
    }

    public static Map<String, Integer> getUserScoresBySessionId(int examSessionId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT u.username, COUNT(ea.id) AS correct_answers " +
                            "FROM exam_answers ea " +
                            "JOIN choices c ON ea.choice_id = c.id " +
                            "JOIN users u ON ea.user_id = u.id " +
                            "WHERE ea.exam_session_id = :examSessionId " +
                            "AND c.is_correct = 1 " +
                            "GROUP BY ea.user_id " +
                            "ORDER BY correct_answers DESC;")
                    .bind("examSessionId", examSessionId);
            return query.mapToMap().list().stream().collect(Collectors.toMap(
                    map -> (String) map.get("username"),
                    map -> ((Number) map.get("correct_answers")).intValue() // Convert Long to Integer
            ));
        });
    }

    public static boolean checkHasAnswerBySessionIdAndUserIdAndQuestionId(int examSessionId, int userId, int questionId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_answers WHERE exam_session_id = :examSessionId AND user_id = :userId AND question_id = :questionId")
                    .bind("examSessionId", examSessionId)
                    .bind("userId", userId)
                    .bind("questionId", questionId);
            return query.mapToBean(ExamAnswerBean.class).findFirst().isPresent();
        });
    }
}
