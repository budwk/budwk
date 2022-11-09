package com.budwk.starter.apiauth.storage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCacheStorage implements CacheStorage {
    //    private
    private ConcurrentHashMap<String, Cache> storage = new ConcurrentHashMap<>();

    private List<Cache> willExpireCache = new LinkedList<>();

    @Override
    public void set(String key, String value) {
        storage.put(key, new Cache(key, value, 0L));
    }

    @Override
    public void set(String key, String value, long expireSeconds) {
        long now = System.currentTimeMillis();
        Cache cache = new Cache(key, value, now + expireSeconds * 1000L);
        storage.put(key, cache);
        willExpireCache.add(cache);
    }

    @Override
    public String get(String key) {
        Cache cache = storage.get(key);
        if (null != cache) {
            long now = System.currentTimeMillis();
            if (cache.isExpire(now)) {
                storage.remove(key);
                removeExpired(now);
                return null;
            }
        }
        return null;
    }

    private void removeExpired(long now) {
        Iterator<Cache> iterator = willExpireCache.iterator();
        while (iterator.hasNext()) {
            Cache cache = iterator.next();
            if (cache.isExpire(now)) {
                iterator.remove();
                storage.remove(cache.key);
            }
        }
    }

    class Cache {
        String key;
        String value;
        long expireAt;

        public Cache(String key, String value, long expireAt) {
            this.key = key;
            this.value = value;
            this.expireAt = expireAt;
        }

        boolean isExpire(long now) {
            return this.expireAt < now;
        }
    }
}
