package com.budwk.starter.apiauth.storage;

public interface CacheStorage {

    void set(String key, String value);

    void set(String key, String value, long expireSeconds);

    String get(String key);

}
