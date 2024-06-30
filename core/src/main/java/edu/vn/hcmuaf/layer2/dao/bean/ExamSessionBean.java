package edu.vn.hcmuaf.layer2.dao.bean;

import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ExamSessionBean {
    @ColumnName("id")
    private int id;
    @ColumnName("exam_id")
    private int examId;
    @ColumnName("host_id")
    private int hostId;
    @ColumnName("status")
    private int status;
    @ColumnName("start_time")
    private Timestamp startTime;
    @ColumnName("end_time")
    private Timestamp endTime;
}
