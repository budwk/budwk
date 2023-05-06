package com.budwk.app.device.handler.common.codec;

/**
 * 缓存
 * @author wizzer.cn
 * @author zyang
 */
public interface CacheStore {
    /**
     * @return 缓存的id
     */
    String getId();

    /**
     * 设置缓存。不会过期
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    boolean set(String key, Object value);

    /**
     * 设置缓存。指定缓存时间
     *
     * @param key          键
     * @param value        值
     * @param expireSecond 缓存秒数
     * @return 是否成功
     */
    boolean set(String key, Object value, long expireSecond);

    /**
     * 设置有效期
     *
     * @param key          键
     * @param expireSecond 缓存秒数
     * @return 是否成功
     */
    boolean expire(String key, long expireSecond);

    /**
     * 返回剩余过期时间
     *
     * @param key 键
     * @return 缓存秒数。-2 表示key不存在，-1 表示无过期时间
     */
    long ttl(String key);

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 获取缓存值并强转为对应的类型
     *
     * @param key  键
     * @param type 类型
     * @return 值
     * @throws ClassCastException
     */
    <T> T get(String key, Class<T> type);

    /**
     * 移除
     *
     * @param key
     * @return
     */
    Object del(String key);
}
