package edu.vn.hcmuaf.layer2.redis;

import redis.clients.jedis.Jedis;

public abstract class  JedisServer {
    protected Jedis getConnection() {
        return new Jedis("localhost", 6379);
    }

}
