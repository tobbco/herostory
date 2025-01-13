package org.herostory.db.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 工具类
 */
public final class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static JedisPool jedisPool;

    static {
        new RedisUtil().init();
    }
    private RedisUtil() {

    }

    private void init() {
        try {
            jedisPool = new JedisPool(REDIS_HOST, REDIS_PORT);
        } catch (Exception e) {
            logger.error("初始化jedis异常", e);
        }
    }


    /**
     * 获取redis实例,从redis池中获取
     * @return redis实例
     */
    public static Jedis getRedis() {
        if (null == jedisPool) {
            logger.warn("jedis pool 没有初始化");
        }
        return jedisPool.getResource();
    }


}
