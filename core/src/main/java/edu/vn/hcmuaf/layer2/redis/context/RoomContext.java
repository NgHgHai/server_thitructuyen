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
    String roomId;
    String hostId;
    String testId;
}
