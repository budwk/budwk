package com.budwk.app.device.handler.impl;

import com.budwk.app.device.handler.common.codec.CacheStore;
import org.nutz.castor.Castors;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wizzer.cn
 */
public class RedissionCacheStore implements CacheStore {
    private String id;
    private RedissonClient redissonClient;

    private RMapCache<String, Object> cache;

    public RedissionCacheStore(String id, RedissonClient redissonClient) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(redissonClient);
        this.id = id;
        this.redissonClient = redissonClient;
        this.cache = this.redissonClient.getMapCache(this.id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean set(String key, Object value) {
        cache.put(key, value);
        return true;
    }

    @Override
    public boolean set(String key, Object value, long expireSecond) {
        cache.put(key, value, expireSecond, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean expire(String key, long expireSecond) {
        Object v = get(key);
        if (null == v) {
            return false;
        }
        set(key, v, expireSecond);

        return true;
    }

    @Override
    public long ttl(String key) {
        return cache.remainTimeToLive(key) / 1000L;
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return Castors.me().castTo(get(key), type);
    }

    @Override
    public Object del(String key) {
        return cache.remove(key);
    }
}
