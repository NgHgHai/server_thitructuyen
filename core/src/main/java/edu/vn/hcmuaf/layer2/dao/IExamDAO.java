package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;

public interface IExamDAO {

    @SqlUpdate("INSERT INTO exams (title, description, image_url, user_id, status) VALUES (:title, :description, :imageUrl, :userId, :status)")
    @GetGeneratedKeys
    int insertExam(@BindBean ExamBean exam);

    @SqlQuery("SELECT * FROM exams WHERE id = :id AND status != -1")
    @RegisterBeanMapper(ExamBean.class)
    ExamBean getExamById(@Bind("id") int id);

    @SqlUpdate("UPDATE exams SET title = :title, description = :description, image_url = :imageUrl, user_id = :userId, status = :status WHERE id = :id AND status != -1")
    int updateExam(@BindBean ExamBean exam);

    @SqlUpdate("UPDATE exams SET status = -1 WHERE id = :id")
    int deleteExam(@Bind("id") int id);

    @SqlQuery("SELECT * FROM exams WHERE status != -1")
    @RegisterBeanMapper(ExamBean.class)
    List<ExamBean> getAllExams();

    default ExamBean getCompleteExamById(int id, Jdbi jdbi) {
        try (Handle handle = jdbi.open()) {
            // lay thong tin bai thi
            ExamBean exam = handle.createQuery("SELECT * FROM exams WHERE id = :id AND status != -1")
                    .bind("id", id)
                    .mapToBean(ExamBean.class)
                    .findOnly();

            if (exam != null) {
                // lay dạnh sach cau hoi cua bai thi
                List<QuestionBean> questions = handle.createQuery("SELECT * FROM questions WHERE exam_id = :examId AND status != -1 ORDER BY question_index")
                        .bind("examId", exam.getId())
                        .mapToBean(QuestionBean.class)
                        .list();

                for (QuestionBean question : questions) {
                    // lay danh sach lua chon cua cau hoi
                    List<ChoiceBean> choices = handle.createQuery("SELECT * FROM choices WHERE question_id = :questionId AND status != -1 ORDER BY choice_index")
                            .bind("questionId", question.getId())
                            .mapToBean(ChoiceBean.class)
                            .list();
                    question.setChoices(choices);
                }

                exam.setQuestions(questions);
            }

            return exam;
        }
    }

    @SqlQuery("SELECT * FROM exams WHERE user_id = :userId AND status != -1")
    List<ExamBean> getAllExamsByUserId(int userId);
}
