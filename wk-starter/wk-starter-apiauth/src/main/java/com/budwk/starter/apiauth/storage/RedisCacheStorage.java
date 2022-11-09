package com.budwk.starter.apiauth.storage;

import org.nutz.integration.jedis.RedisService;

public class RedisCacheStorage implements CacheStorage {

    private RedisService redisService;

    public RedisCacheStorage(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void set(String key, String value) {

        redisService.set(key, value);
    }

    @Override
    public void set(String key, String value, long expireSeconds) {
        redisService.setex(key, (int) expireSeconds, value);
    }

    @Override
    public String get(String key) {
        return redisService.get(key);
    }
}
