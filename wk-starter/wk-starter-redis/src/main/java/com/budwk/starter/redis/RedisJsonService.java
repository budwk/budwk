package com.budwk.starter.redis;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.Path;
import redis.clients.jedis.json.Path2;

@IocBean(create = "init")
public class RedisJsonService {
    @Inject
    private RedisStarter redisStarter;
    private UnifiedJedis client;

    public void init() {
        client = redisStarter.getUnifiedJedis();
    }

    public <T> String setObject(String key, T object) {
        return client.jsonSet(key, Path.ROOT_PATH, object);
    }

    public <T> String updateField(String key, String path, T value) {
        return client.jsonSetWithEscape(key, Path2.of(path), value);
    }

    public long delObject(String key) {
        return client.jsonDel(key);
    }

    public long delField(String key, String path) {
        return client.jsonDel(key, Path2.of(path));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return client.jsonGet(key, clazz);
    }

    public Object getField(String key, String path) {
        return client.jsonGet(key, Path2.of(path));
    }

    public String setEx(String key, String value, int expire) {
        return client.setex(key, expire, value);
    }

    public String get(String key) {
        return client.get(key);
    }

    public long del(String key) {
        return client.del(key);
    }

    public long hset(String key, String field, String value) {
        return client.hset(key, field, value);
    }

    public boolean exist(String key) {
        return client.exists(key);
    }
}
