package edu.vn.hcmuaf.layer2.redis.context;

import edu.vn.hcmuaf.layer2.proto.Proto;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SessionContext implements Serializable {
    private String sessionID; // sessionID la id cua server : id cua session
    private Proto.User user;
    private int roomId;
    private String socketID; // socketID la id cua server
}
