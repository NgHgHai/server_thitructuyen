package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.ExamSessionBean;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;

public interface IExamSessionDAO {

    @SqlUpdate("INSERT INTO exam_sessions (exam_id, host_id, status, start_time, end_time) VALUES (:examId, :hostId, :status, :startTime, :endTime)")
    @GetGeneratedKeys
    int insertExamSession(@BindBean ExamSessionBean examSession);

    @SqlQuery("SELECT * FROM exam_sessions WHERE id = :id")
    @RegisterBeanMapper(ExamSessionBean.class)
    ExamSessionBean getExamSessionById(@Bind("id") int id);

    @SqlUpdate("UPDATE exam_sessions SET exam_id = :examId, host_id = :hostId, status = :status, start_time = :startTime, end_time = :endTime WHERE id = :id")
    void updateExamSession(@BindBean ExamSessionBean examSession);

    @SqlUpdate("UPDATE exam_sessions SET status = -1 WHERE id = :id")
    void deleteExamSession(@Bind("id") int id);
    @SqlQuery("SELECT * FROM exam_sessions WHERE exam_id = :examId")
    List<ExamSessionBean> getExamSessionsByExamId(@Bind("examId") int examId);
    @SqlQuery("SELECT * FROM exam_sessions WHERE host_id = :hostId")
    List<ExamSessionBean> getExamSessionsByHostId(@Bind("hostId") int hostId);
    @SqlQuery("SELECT * FROM exam_sessions WHERE exam_id = :examId AND host_id = :hostId")
    List<ExamSessionBean> geExamSessionsByExamIdAndHostId(@Bind("examId") int examId, @Bind("hostId") int hostId);
}
