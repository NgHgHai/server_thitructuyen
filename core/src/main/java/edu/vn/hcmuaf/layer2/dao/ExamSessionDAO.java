package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ExamSessionBean;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.util.List;

public class ExamSessionDAO extends PoolConnectDAO {
    private static Jdbi jdbi = getJdbi();

    public static int insertExamSession(ExamSessionBean examSession) {
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("INSERT INTO exam_sessions (exam_id, host_id, status, start_time, end_time) VALUES (:examId, :hostId, :status, :startTime, :endTime)")
                    .bindBean(examSession);
            return update.executeAndReturnGeneratedKeys("id").mapTo(int.class).findOnly();
        });
    }

    public static ExamSessionBean getExamSessionById(int id) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_sessions WHERE id = :id")
                    .bind("id", id);
            return query.mapToBean(ExamSessionBean.class).findFirst().orElse(null);
        });
    }

    public static void updateExamSession(ExamSessionBean examSession) {
        jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exam_sessions SET exam_id = :examId, host_id = :hostId, status = :status, start_time = :startTime, end_time = :endTime WHERE id = :id")
                    .bindBean(examSession);
            return update.execute();
        });
    }

    public static void deleteExamSession(int id) {
        jdbi.withHandle(handle -> {
            Update update = handle.createUpdate("UPDATE exam_sessions SET status = -1 WHERE id = :id")
                    .bind("id", id);
            return update.execute();
        });
    }

    public static List<ExamSessionBean> getExamSessionsByExamId(int examId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_sessions WHERE exam_id = :examId")
                    .bind("examId", examId);
            return query.mapToBean(ExamSessionBean.class).list();
        });
    }

    public static List<ExamSessionBean> getExamSessionsByHostId(int hostId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_sessions WHERE host_id = :hostId")
                    .bind("hostId", hostId);
            return query.mapToBean(ExamSessionBean.class).list();
        });
    }

    public static List<ExamSessionBean> geExamSessionsByExamIdAndHostId(int examId, int hostId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("SELECT * FROM exam_sessions WHERE exam_id = :examId AND host_id = :hostId")
                    .bind("examId", examId)
                    .bind("hostId", hostId);
            return query.mapToBean(ExamSessionBean.class).list();
        });
    }
// select *
//from exam_sessions
//where id in (SELECT DISTINCT exam_sessions.id
//             FROM exam_sessions
//                      JOIN exam_answers ON exam_sessions.id = exam_answers.exam_session_id
//             WHERE exam_answers.user_id = 4)
    public static List<ExamSessionBean> getExamSessionIdsByUserId(int userId) {
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery("select * " +
                            "from exam_sessions " +
                            "where id in (SELECT DISTINCT exam_sessions.id " +
                            "    FROM exam_sessions " +
                            "    JOIN exam_answers ON exam_sessions.id = exam_answers.exam_session_id " +
                            "    WHERE exam_answers.user_id = :userId   )")
                    .bind("userId", userId);
            return query.mapToBean(ExamSessionBean.class).list();
        });
    }
}
