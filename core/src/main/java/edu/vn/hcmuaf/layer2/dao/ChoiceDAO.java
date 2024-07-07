package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ChoiceBean;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.util.List;

public class ChoiceDAO extends PoolConnectDAO {
    private static Jdbi jdbi = getJdbi();


    public static int insertChoice(ChoiceBean choice) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("INSERT INTO choices (question_id, choice_index, choice_text, image_url, is_correct, status) VALUES (:questionId, :choiceIndex, :choiceText, :imageUrl, :isCorrect, :status)")
                    .bind("questionId", choice.getQuestionId())
                    .bind("choiceIndex", choice.getChoiceIndex())
                    .bind("choiceText", choice.getChoiceText())
                    .bind("imageUrl", choice.getImageUrl())
                    .bind("isCorrect", choice.isCorrect() ? 1 : 0)
                    .bind("status", choice.getStatus());

            return update.executeAndReturnGeneratedKeys("id").mapTo(int.class).findOnly();
        });
    }

    public static ChoiceBean getChoiceById(int id) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM choices WHERE id = :id AND status != -1")
                    .bind("id", id);
            int isCorrect = handle.createQuery("SELECT is_correct FROM choices WHERE id = :id AND status != -1")
                    .bind("id", id).mapTo(int.class).findOnly();
            ChoiceBean choice = query.mapToBean(ChoiceBean.class).findFirst().orElse(null);
            choice.setCorrect(isCorrect == 1);
            return choice;
        });
    }

    public static void updateChoice(ChoiceBean choice) {

        System.out.println(choice.getChoiceIndex() + "aaaaaaaaaaaaaa");
        jdbi.useHandle(handle -> {
            Update update = handle.createUpdate("UPDATE choices SET choice_text = :choiceText, image_url = :imageUrl, is_correct = :isCorrect, status = :status WHERE question_id = :questionId AND choice_index = :choiceIndex AND status != -1")
                    .bind("isCorrect", choice.isCorrect() ? 1 : 0)
                    .bind("questionId", choice.getQuestionId())
                    .bind("choiceIndex", choice.getChoiceIndex())
                    .bind("choiceText", choice.getChoiceText())
                    .bind("imageUrl", choice.getChoiceText())
                    .bind("status", choice.getStatus());
            System.out.println(choice);
            update.execute();
            System.out.println("da update roi nha");
        });
    }

    public static void deleteChoice(int id) {
        jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE choices SET status = -1 WHERE id = :id")
                    .bind("id", id);
            update.execute();
            return null;
        });
    }

    public static int deleteChoicesByQuestionId(int questionId) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE choices SET status = -1 WHERE question_id = :questionId")
                    .bind("questionId", questionId);
            return update.execute();
        });
    }

    public static List<ChoiceBean> getChoicesByQuestionId(int questionId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM choices WHERE question_id = :questionId AND status != -1 ORDER BY choice_index")
                    .bind("questionId", questionId);
            List<ChoiceBean> choices = query.mapToBean(ChoiceBean.class).list();
            for (ChoiceBean choice : choices) {
                int isCorrect = handle.createQuery("SELECT is_correct FROM choices WHERE id = :id AND status != -1")
                        .bind("id", choice.getId()).mapTo(int.class).findOnly();
                choice.setCorrect(isCorrect == 1);
            }
            return choices;
        });
    }

    public static void main(String[] args) {
        System.out.print(ChoiceDAO.getChoiceById(5).isCorrect());
    }
}
