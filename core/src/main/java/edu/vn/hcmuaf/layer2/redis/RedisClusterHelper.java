package edu.vn.hcmuaf.layer2.redis;

import edu.vn.hcmuaf.layer2.redis.context.SessionContext;
import lombok.SneakyThrows;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.redisnode.RedisCluster;
import org.redisson.config.Config;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.providers.ClusterConnectionProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.vn.hcmuaf.layer2.CompressUtils.compress;
import static edu.vn.hcmuaf.layer2.CompressUtils.decompress;

import static java.util.Objects.nonNull;


public abstract class RedisClusterHelper {
    private static JedisCluster cluster;
    private static RedissonClient redisson;
    private static ClusterConnectionProvider provider;
//    private final Logger logger = Logger.getLogger(RedisClusterHelper.class);

    protected RedisClusterHelper() {
    }

    public static void closeConnection() {
        if (nonNull(cluster)) {
            cluster.close();
            cluster = null;
        }
        if (nonNull(redisson)) {
            redisson.shutdown();
            redisson = null;
        }
    }

    protected JedisCluster getConnection() {
        if (cluster == null) {
            System.out.println("dang tao");
            Set<HostAndPort> jedisClusterNode = new HashSet<>();
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7001));
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7002));
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7003));
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7004));
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7005));
            jedisClusterNode.add(new HostAndPort(RedisProperties.getHost(), 7006));


            var poolConfig = new GenericObjectPoolConfig<Connection>();
            poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(60)); // thoi gian giua cac lan chay thread kiem tra cac connection
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setMaxWait(Duration.ofMillis(2000));

            poolConfig.setMaxTotal(80);
            poolConfig.setMaxIdle(35);// số kết nối tối đa không sử dụng được giữ trong redis
            poolConfig.setMinIdle(35);

            provider = new ClusterConnectionProvider(jedisClusterNode, DefaultJedisClientConfig.builder().password(RedisProperties.getPassword()).build(), poolConfig);
            cluster = new JedisCluster(provider, 30, Duration.ofMillis(1000));
        }
        return cluster;
    }

    protected RedissonClient getRedissonClient() {
        List<String> nodes = new ArrayList<>();
        nodes.add("redis://" + RedisProperties.getHost() + ":7001");
        nodes.add("redis://" + RedisProperties.getHost() + ":7002");
        nodes.add("redis://" + RedisProperties.getHost() + ":7003");
        nodes.add("redis://" + RedisProperties.getHost() + ":7004");
        nodes.add("redis://" + RedisProperties.getHost() + ":7005");
        nodes.add("redis://" + RedisProperties.getHost() + ":7006");
        if (Objects.isNull(redisson)) {
            Config config = new Config();
            config.setThreads(8);//thử nghiệm
            config.useClusterServers();
            config.useClusterServers().setNodeAddresses(List.of("redis://" + RedisProperties.getHost() + ":" + RedisProperties.getPort()));
            config.useClusterServers().setPassword(RedisProperties.getPassword());
            redisson = Redisson.create(config);
        }
        return redisson;
    }

    public boolean healthCheck() {
        for (Map.Entry<String, ConnectionPool> node : cluster.getClusterNodes().entrySet()) {
            try (Connection jedis = node.getValue().getResource()) {
                if (!jedis.ping()) return false;
                //Node is OK
            } catch (JedisConnectionException jce) {
                //Node FAILS
                return false;
            }
        }
        return true;
    }

    public boolean containKey(String key, Class<?> c) {
        try {
            return getConnection().sismember(c.getCanonicalName(), key);
        } catch (Exception e) {
//            logger.error("containKey key=" + key + " class=" + c.getSimpleName());
            throw new RuntimeException(e);
        }
    }

    protected void resetConnection() {
        cluster = null;
    }

    protected <T extends Serializable> void set(String key, T v) {
        if (Objects.isNull(v)) {
            throw new RuntimeException("Redis set null value key=" + key);
        }
        getConnection().set(determineObjKey(key, v.getClass()), compress(v));
    }

    protected <T extends Serializable> T get(String key, Class<T> tClass) {
        try {
            return decompress(getConnection().get(determineObjKey(key, tClass)), tClass);
        } catch (Exception e) {
            getConnection().srem(tClass.getCanonicalName(), key);
//            logger.error("redis get value error, key=" + key + ", class=" + tClass.getSimpleName());
            return null;
        }
    }

    protected <T extends Serializable> List<T> get(String key[], Class<T> tClass) {
        List<byte[]> keys = Arrays.stream(key).map(k -> determineObjKey(k, tClass)).collect(Collectors.toList());
        return new ArrayList<>(get(keys, tClass).values());
    }

    private <T extends Serializable> Map<String, T> get(List<byte[]> keys, Class<T> c) {
        final ClusterPipeline pipeline = createOneTimePipeline();
        List<List<Object>> collect = keys.stream().map(k -> List.of(k, pipeline.get(k))).collect(Collectors.toList());
        pipeline.sync();
        pipeline.close();
        if (collect.size() == 0) return new HashMap<>();
        Stream<List<Object>> stream = collect.stream().filter(Objects::nonNull).filter(objects -> nonNull(objects.get(0)) && nonNull(objects.get(1)));

        return stream.collect(Collectors.toMap(keyAndValue -> {
            try {
                return new String((byte[]) keyAndValue.get(0));
            } catch (Exception e) {
//                        logger.error("keyAndValue.toString() data=" + keyAndValue);
                throw new RuntimeException(e);
            }
        }, keyAndValue -> {
            try {
                return decompress(((Response<byte[]>) keyAndValue.get(1)).get(), c);
            } catch (Exception e) {
//                        logger.error("((Response<String>) keyAndValue[1]).get() data=" + keyAndValue);
                throw new RuntimeException(e);
            }
        }));
    }

    private <T extends Serializable> Map<String, T> get(Set<String> keys, Class<T> c) {
        Map<String, T> map = new HashMap<>();
        keys.forEach(k -> {
            if (k == null) return;
            map.put(k, get(k, c));
        });
        return map;
    }

    protected <T extends Serializable> T delete(String key, Class<T> tClass) {
        T t = get(key, tClass);
        if (t == null) return null;
        getConnection().del(determineObjKey(key, tClass));
        return t;
    }

    protected <T extends Serializable> List<T> delete(String[] keys, Class<T> tClass) {
        List<T> t = get(keys, tClass);
        if (t == null) return Collections.EMPTY_LIST;
        //TODO: improve performance by pipeline
        Arrays.stream(keys).forEach(k -> getConnection().del(determineObjKey(k, tClass)));
        return t;
    }


    protected <T extends Serializable> void saveObj(String key, T v) {
        getConnection().sadd(v.getClass().getCanonicalName(), key);
        this.set(key, v);
    }

    protected <T extends Serializable> Map<String, T> getAllObj(final Class<T> c) {
        List<byte[]> keys = getConnection().smembers(c.getCanonicalName()).stream().map(k -> determineObjKey(k, c)).collect(Collectors.toList());
        return get(keys, c);
    }

    protected <T> Set<String> getKeys(final Class<T> c) {
        return getConnection().smembers(c.getCanonicalName());
    }

    private ClusterPipeline createOneTimePipeline() {
        return new ClusterPipeline(provider);
    }

    protected <T extends Serializable> T deleteObj(String key, Class<T> tClass) {
        if (key == null) throw new RuntimeException("Key is null");
        T t = delete(key, tClass);
        getConnection().srem(tClass.getCanonicalName(), key);
        return t;
    }

    protected <T extends Serializable> List<T> deleteObj(String[] keys, Class<T> tClass) {
        if (keys == null) throw new RuntimeException("Key is null");
        if (keys.length == 0) return Collections.EMPTY_LIST;
        List<T> t = delete(keys, tClass);
        getConnection().srem(tClass.getCanonicalName(), keys);
        return t;
    }

    /**
     * them 1 phan tu vao hash map o redis co key la key va field la field voi gia tri la value cua kieu T
     */
    protected <T extends Serializable> void hset(String key, String field, T t) {
        getConnection().hset(key.getBytes(), field.getBytes(), compress(t));
    }

    /**
     * lay gia tri cua 1 field trong hash map co key la key va field la field voi kieu T la keu du lieu cua value
     */
    protected <T extends Serializable> T hget(String key, String field, Class<T> t) {
        byte[] s = getConnection().hget(key.getBytes(), field.getBytes());
        return decompress(s, t);
    }

    /**
     * lay tat ca cac gia tri cua hash map co key la key voi kieu T la kieu du lieu cua value
     */
    protected <T extends Serializable> Map<String, T> hgetAll(String key, Class<T> c) {
        Map<byte[], byte[]> all = getConnection().hgetAll(key.getBytes());
        Map<String, T> data = new HashMap<>();
        all.forEach((s, v) -> data.put(new String(s), decompress(v, c)));
        return data;
    }

    /**
     * lay ra danh sach cac field cua hash map co key la key
     */
    protected Set<String> hkeys(String key) {
        return getConnection().hkeys(key);
    }

    /**
     * xoa 1 field cua hash map co key la key va field la field
     */
    protected void hdel(String key, String... field) {
        getConnection().hdel(key, field);
    }

    protected <T> byte[] determineObjKey(String key, Class<T> c) {
        return (c.getCanonicalName() + ":" + key).getBytes();
    }

    protected <T> String determineLockKey(String key, Class<T> c) {
        if (Objects.isNull(key)) {
            throw new RuntimeException("key is null");
        }
        return c.getCanonicalName() + ":" + key + ":Locker";
    }

    protected <T> RLock lock(String key, Class<T> c) {
        RLock lock = getRedissonClient().getSpinLock(determineLockKey(key, c));
        try {
            boolean isLock = lock.tryLock(5, 3, TimeUnit.SECONDS);
//            boolean isLock = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLock) {
//                logger.warn("Lock fail, key=" + key + " class=" + c.getSimpleName());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return lock;
    }

    protected long increaseInt(String key) {
        try {
            return getConnection().incr(key);
        } catch (Exception e) {
//            logger.error("increaseInt key=" + key);
            throw new RuntimeException(e);
        }
    }

    protected double increaseDouble(String key, double value) {
        try {
            return getConnection().incrByFloat(key, value);
        } catch (Exception e) {
//            logger.error("increaseInt key=" + key);
            throw new RuntimeException(e);
        }
    }

    protected long increaseStep(String key, int step) {
        try {
            return getConnection().incrBy(key, step);
        } catch (Exception e) {
//            logger.error("increaseStep key=" + key);
            throw new RuntimeException(e);
        }
    }

    protected long decreaseInt(String key) {
        try {
            return getConnection().decr(key);
        } catch (Exception e) {
//            logger.error("decreaseInt key=" + key);
            throw new RuntimeException(e);
        }
    }

    protected double decreaseDouble(String key, double value) {
        try {
            return getConnection().incrByFloat(key, -value);
        } catch (Exception e) {
//            logger.error("decreaseInt key=" + key);
            throw new RuntimeException(e);
        }
    }

    protected long decreaseStep(String key, int step) {
        try {
            return getConnection().decrBy(key, step);
        } catch (Exception e) {
//            logger.error("decreaseStep key=" + key);
            throw new RuntimeException(e);

        }

    }

    protected void deleteKey(String key) {
        getConnection().del(key);
    }


    protected long hIncreaseInt(String key1, String key2) {
        return hIncreaseStep(key1, key2, 1);
    }


    protected long hDecreaseInt(String key1, String key2) {
        return hDecreaseStep(key1, key2, 1);
    }

    protected long hIncreaseStep(String key1, String key2, int step) {
        try {
            return getConnection().hincrBy(key1, key2, step);
        } catch (Exception e) {
//            logger.error("increaseStep key=" + key1 + " - " + key2);
            throw new RuntimeException(e);
        }
    }

    protected long hDecreaseStep(String key1, String key2, int step) {
        try {
            return getConnection().hincrBy(key1, key2, -step);
        } catch (Exception e) {
//            logger.error("decreaseStep key=" + key1 + " - " + key2);
            throw new RuntimeException(e);

        }

    }

    protected void hDeleteKey(String key1, String key2) {
        getConnection().hdel(key1, key2);
    }


    private void sleep(long millisecond) throws InterruptedException {
        Thread.sleep(millisecond);
    }


    public static class RedisProperties {
        private static final Logger logger = Logger.getLogger(RedisProperties.class);

        private static final Properties prop = new Properties();

        static {

            try {
                File file = new File("/redis.properties");
                if (file.exists()) {
                    prop.load(new FileInputStream(file));
                } else {
                    prop.load(RedisProperties.class.getClassLoader().getResourceAsStream("redis.properties"));
                }
            } catch (IOException e) {
                logger.error("Load properties fail !", e);
                throw new RuntimeException(e);
            }
            logger.info("Properties: " + prop);

        }

        public static String getHost() {
            if (System.getenv("redis_host") != null) {
                System.out.println("redis_host: " + System.getenv("redis_host"));
                return System.getenv("redis_host");
            }
            return prop.get("redis.host").toString();
        }

        public static int getPort() {
            if (System.getenv("redis_port") != null) {
                System.out.println("redis_port: " + System.getenv("redis_port"));
                return Integer.parseInt(System.getenv("redis_port"));
            }
            return Integer.parseInt(prop.get("redis.port").toString());
        }

        public static String getPassword() {
            if (System.getenv("redis_password") != null) {
                System.out.println("redis_password: " + System.getenv("redis_password"));
                return System.getenv("redis_password");
            }
            return prop.get("redis.password").toString();
        }
    }
}
