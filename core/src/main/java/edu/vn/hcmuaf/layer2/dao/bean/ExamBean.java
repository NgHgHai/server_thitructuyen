package edu.vn.hcmuaf.layer2.dao.bean;

import edu.vn.hcmuaf.layer2.proto.Proto;
import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ExamBean {
    @ColumnName("id")
    private int id;
    @ColumnName("title")
    private String title;
    @ColumnName("description")
    private String description;
    @ColumnName("image_url")
    private String imageUrl;
    @ColumnName("created_at")
    private Timestamp createdAt;
    @ColumnName("updated_at")
    private Timestamp updatedAt;
    @ColumnName("user_id")
    private int userId;
    @ColumnName("status")
    private int status;


    private List<QuestionBean> questions;


}
