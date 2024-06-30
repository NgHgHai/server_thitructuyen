package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.*;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;

public interface IChoiceDAO {

    @SqlUpdate("INSERT INTO choices (question_id, choice_index, choice_text, image_url, is_correct, status) VALUES (:questionId, :choiceIndex, :choiceText, :imageUrl, :isCorrect, :status)")
    @GetGeneratedKeys
    int insertChoice(@BindBean ChoiceBean choice);

    @SqlQuery("SELECT * FROM choices WHERE id = :id AND status != -1")
    @RegisterBeanMapper(ChoiceBean.class)
    ChoiceBean getChoiceById(@Bind("id") int id);

    @SqlUpdate("UPDATE choices SET question_id = :questionId, choice_index = :choiceIndex, choice_text = :choiceText, image_url = :imageUrl, is_correct = :isCorrect, status = :status WHERE id = :id AND status != -1")
    void updateChoice(@BindBean ChoiceBean choice);

    @SqlUpdate("UPDATE choices SET status = -1 WHERE id = :id")
    void deleteChoice(@Bind("id") int id);
    @SqlUpdate("UPDATE choices SET status = -1 WHERE question_id = :questionId")
    int deleteChoicesByQuestionId(int questionId);

    @SqlQuery("SELECT * FROM choices WHERE question_id = :questionId AND status != -1 ORDER BY choice_index")
    @RegisterBeanMapper(ChoiceBean.class)
    List<ChoiceBean> getChoicesByQuestionId(@Bind("questionId") int questionId);
}
