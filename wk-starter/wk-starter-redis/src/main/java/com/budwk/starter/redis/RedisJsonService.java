package com.budwk.starter.redis;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.Path;
import redis.clients.jedis.json.Path2;

@IocBean
public class RedisJsonService {
    @Inject
    private RedisStarter redisStarter;

    public <T> String setObject(String key, T object) {
        return redisStarter.getUnifiedJedis().jsonSet(key, Path.ROOT_PATH, object);
    }

    public <T> String updateField(String key, String path, T value) {
        return redisStarter.getUnifiedJedis().jsonSetWithEscape(key, Path2.of(path), value);
    }

    public long delObject(String key) {
        return redisStarter.getUnifiedJedis().jsonDel(key);
    }

    public long delField(String key, String path) {
        return redisStarter.getUnifiedJedis().jsonDel(key, Path2.of(path));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return redisStarter.getUnifiedJedis().jsonGet(key, clazz);
    }

    public Object getField(String key, String path) {
        return redisStarter.getUnifiedJedis().jsonGet(key, Path2.of(path));
    }

    public String setEx(String key, String value, int expire) {
        return redisStarter.getUnifiedJedis().setex(key, expire, value);
    }

    public String get(String key) {
        return redisStarter.getUnifiedJedis().get(key);
    }

    public long del(String key) {
        return redisStarter.getUnifiedJedis().del(key);
    }

    public long hset(String key, String field, String value) {
        return redisStarter.getUnifiedJedis().hset(key, field, value);
    }

    public boolean exist(String key) {
        return redisStarter.getUnifiedJedis().exists(key);
    }
}
