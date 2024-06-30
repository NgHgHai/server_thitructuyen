package edu.vn.hcmuaf.layer2.redis.context;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Getter
@Setter
public class RoomContext implements Serializable {
    int roomId;
    int hostId;
    int testId;
    int currentQuestionId;
    int status;
    int examSessionId;
}
