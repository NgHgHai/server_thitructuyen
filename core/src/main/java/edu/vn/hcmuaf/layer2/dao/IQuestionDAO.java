package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.*;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;

public interface IQuestionDAO {

    @SqlUpdate("INSERT INTO questions (exam_id, question_index, question_text, image_url, time, status) VALUES (:examId, :questionIndex, :questionText, :imageUrl, :time, :status)")
    @GetGeneratedKeys
    int insertQuestion(@BindBean QuestionBean question);

    @SqlQuery("SELECT * FROM questions WHERE id = :id AND status != -1")
    @RegisterBeanMapper(QuestionBean.class)
    QuestionBean getQuestionById(@Bind("id") int id);

    @SqlUpdate("UPDATE questions SET exam_id = :examId, question_index = :questionIndex, question_text = :questionText, image_url = :imageUrl, time = :time, status = :status WHERE id = :id AND status != -1")
    int updateQuestion(@BindBean QuestionBean question);

    @SqlUpdate("UPDATE questions SET status = -1 WHERE id = :id")
    int deleteQuestion(@Bind("id") int id);

    @SqlQuery("SELECT * FROM questions WHERE exam_id = :examId AND status != -1 ORDER BY question_index")
    @RegisterBeanMapper(QuestionBean.class)
    List<QuestionBean> getQuestionsByExamId(@Bind("examId") int examId);

    default QuestionBean getCompleteQuestionById(int id, IChoiceDAO choiceDAO) {
        QuestionBean question = getQuestionById(id);
        if (question != null) {
            List<ChoiceBean> choices = choiceDAO.getChoicesByQuestionId(question.getId());
            question.setChoices(choices);
        }
        return question;
    }
}
