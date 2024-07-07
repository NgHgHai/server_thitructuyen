package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import edu.vn.hcmuaf.layer2.dao.bean.ExamBean;
import edu.vn.hcmuaf.layer2.dao.bean.QuestionBean;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.util.List;

public class ExamDAO extends PoolConnectDAO {
    private static Jdbi jdbi = getJdbi();

    public static int insertExam(ExamBean exam) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("INSERT INTO exams (title, description, image_url, user_id, status) VALUES (:title, :description, :imageUrl, :userId, :status)")
                    .bindBean(exam);
            return update.executeAndReturnGeneratedKeys("id").mapTo(int.class).findOnly();
        });
    }

    public static ExamBean getExamById(int id) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exams WHERE id = :id AND status != -1")
                    .bind("id", id);
            return query.mapToBean(ExamBean.class).findFirst().orElse(null);
        });
    }

    public static int updateExam(ExamBean exam) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exams SET title = :title, description = :description, image_url = :imageUrl, user_id = :userId, status = :status WHERE id = :id AND status != -1")
                    .bindBean(exam);
            return update.execute();
        });
    }

    public static int deleteExam(int id) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exams SET status = -1 WHERE id = :id")
                    .bind("id", id);
            return update.execute();
        });
    }

    public static List<ExamBean> getAllExams() {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exams WHERE status != -1");
            return query.mapToBean(ExamBean.class).list();
        });
    }

    public static List<ExamBean> getAllExamsByUserId(int userId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exams WHERE user_id = :userId AND status != -1")
                    .bind("userId", userId);
            return query.mapToBean(ExamBean.class).list();
        });
    }

    public static ExamBean getCompleteExamById(int id) {
        return jdbi.withHandle(handle -> {
            ExamBean exam = handle.createQuery("SELECT * FROM exams WHERE id = :id AND status != -1")
                    .bind("id", id)
                    .mapToBean(ExamBean.class)
                    .findFirst().orElse(null);

            if (exam != null) {
                List<QuestionBean> questions = handle.createQuery("SELECT * FROM questions WHERE exam_id = :examId AND status != -1 ORDER BY question_index")
                        .bind("examId", exam.getId())
                        .mapToBean(QuestionBean.class)
                        .list();

                for (QuestionBean question : questions) {
                    List<ChoiceBean> choices = handle.createQuery("SELECT * FROM choices WHERE question_id = :questionId AND status != -1 ORDER BY choice_index")
                            .bind("questionId", question.getId())
                            .mapToBean(ChoiceBean.class)
                            .list();
                    question.setChoices(choices);
                }

                exam.setQuestions(questions);
            }

            return exam;
        });
    }
}
