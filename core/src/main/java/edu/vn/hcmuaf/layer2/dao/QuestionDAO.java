package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.util.List;

public class QuestionDAO extends PoolConnectDAO {
    private static Jdbi jdbi = getJdbi();

    public static int insertQuestion(QuestionBean question) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("INSERT INTO questions (exam_id, question_index, question_text, image_url, time, status) VALUES (:examId, :questionIndex, :questionText, :imageUrl, :time, :status)")
                    .bindBean(question);
            return update.executeAndReturnGeneratedKeys("id").mapTo(int.class).findOnly();
        });
    }

    public static QuestionBean getQuestionById(int id) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM questions WHERE id = :id AND status != -1")
                    .bind("id", id);
            return query.mapToBean(QuestionBean.class).findFirst().orElse(null);
        });
    }

    public static int updateQuestion(QuestionBean question) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE questions SET exam_id = :examId, question_index = :questionIndex, question_text = :questionText, image_url = :imageUrl, time = :time, status = :status WHERE id = :id AND status != -1")
                    .bindBean(question);
            return update.execute();
        });
    }

    public static int deleteQuestion(int id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE questions SET status = -1 WHERE id = :id")
                    .bind("id", id);
            return update.execute();
        });
    }

    public static List<QuestionBean> getQuestionsByExamId(int examId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM questions WHERE exam_id = :examId AND status != -1 ORDER BY question_index")
                    .bind("examId", examId);
            return query.mapToBean(QuestionBean.class).list();
        });
    }

    public static QuestionBean getCompleteQuestionById(int id) {
        QuestionBean question = getQuestionById(id);
        if (question != null) {
            List<ChoiceBean> choices = ChoiceDAO.getChoicesByQuestionId(question.getId());
            question.setChoices(choices);
        }
        return question;
    }

    public static QuestionBean getCompleteQuestionByIndexAndExamId(int examId, int i) {
        int questionId = jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT id FROM questions WHERE exam_id = :examId AND question_index = :questionIndex AND status != -1")
                    .bind("examId", examId)
                    .bind("questionIndex", i);
            return query.mapTo(int.class).findFirst().orElse(-1);
        });
        return getCompleteQuestionById(questionId);
    }

    public static int getTotalQuestionByExamId(int examId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT COUNT(*) FROM questions WHERE exam_id = :examId AND status != -1")
                    .bind("examId", examId);
            return query.mapTo(int.class).findFirst().orElse(0);
        });
    }
}
