package com.budwk.starter.redis;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis服务类，封装UnifiedJedis常用操作
 * 支持单机和集群模式
 */
@IocBean
public class RedisService {
    @Inject
    private RedisStarter redisStarter;
    @Inject
    private PropertiesProxy conf;
    
    public boolean isCluster() {
        if ("da".equalsIgnoreCase(conf.get("redis.mode"))) {
            return "cluster".equalsIgnoreCase(conf.get("redis.many." + redisStarter.getCurrentDatasource() + ".mode"));
        } else {
            return "cluster".equalsIgnoreCase(conf.get("redis.mode"));
        }
    }

    // ============== 键值操作 ==============

    /**
     * 设置指定key的值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        redisStarter.getUnifiedJedis().set(key, value);
    }

    /**
     * 设置指定key的值
     *
     * @param key   键
     * @param value 值
     */
    public void set(byte[] key, byte[] value) {
        redisStarter.getUnifiedJedis().set(key, value);
    }

    /**
     * 设置指定key的值，并设置过期时间（秒）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void setex(String key, String value, int seconds) {
        redisStarter.getUnifiedJedis().setex(key, seconds, value);
    }

    /**
     * 设置指定key的值，并设置过期时间（秒）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void setex(byte[] key, byte[] value, int seconds) {
        redisStarter.getUnifiedJedis().setex(key, seconds, value);
    }

    /**
     * 设置指定key的值，并设置过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @param value   值
     */
    public void setex(String key, int seconds, String value) {
        redisStarter.getUnifiedJedis().setex(key, seconds, value);
    }

    /**
     * 设置指定key的值，并设置过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @param value   值
     */
    public void setex(byte[] key, int seconds, byte[] value) {
        redisStarter.getUnifiedJedis().setex(key, seconds, value);
    }

    /**
     * 只有在key不存在时设置key的值
     *
     * @param key   键
     * @param value 值
     * @return 设置成功返回1，失败返回0
     */
    public long setnx(String key, String value) {
        return redisStarter.getUnifiedJedis().setnx(key, value);
    }

    /**
     * 只有在key不存在时设置key的值
     *
     * @param key   键
     * @param value 值
     * @return 设置成功返回1，失败返回0
     */
    public long setnx(byte[] key, byte[] value) {
        return redisStarter.getUnifiedJedis().setnx(key, value);
    }

    /**
     * 设置key的值，可选参数
     *
     * @param key    键
     * @param value  值
     * @param params 参数选项，如NX, XX, EX, PX等
     * @return 设置结果
     */
    public String set(String key, String value, SetParams params) {
        return redisStarter.getUnifiedJedis().set(key, value, params);
    }

    /**
     * 设置key的值，可选参数
     *
     * @param key    键
     * @param value  值
     * @param params 参数选项，如NX, XX, EX, PX等
     * @return 设置结果
     */
    public String set(byte[] key, byte[] value, SetParams params) {
        return redisStarter.getUnifiedJedis().set(key, value, params);
    }

    /**
     * 获取指定key的值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return redisStarter.getUnifiedJedis().get(key);
    }

    /**
     * 获取指定key的值
     *
     * @param key 键
     * @return 值
     */
    public byte[] get(byte[] key) {
        return redisStarter.getUnifiedJedis().get(key);
    }

    /**
     * 删除指定key
     *
     * @param key 键
     * @return 成功删除的key数量
     */
    public long del(String key) {
        return redisStarter.getUnifiedJedis().del(key);
    }

    /**
     * 删除指定key
     *
     * @param key 键
     * @return 成功删除的key数量
     */
    public long del(byte[] key) {
        return redisStarter.getUnifiedJedis().del(key);
    }

    /**
     * 批量删除key
     *
     * @param keys 键数组
     * @return 成功删除的key数量
     */
    public long del(String... keys) {
        return redisStarter.getUnifiedJedis().del(keys);
    }

    /**
     * 批量删除key
     *
     * @param keys 键数组
     * @return 成功删除的key数量
     */
    public long del(byte[]... keys) {
        return redisStarter.getUnifiedJedis().del(keys);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 存在返回true，否则返回false
     */
    public boolean exists(String key) {
        return redisStarter.getUnifiedJedis().exists(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 存在返回true，否则返回false
     */
    public boolean exists(byte[] key) {
        return redisStarter.getUnifiedJedis().exists(key);
    }

    /**
     * 设置key的过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return 设置成功返回1，失败返回0
     */
    public long expire(String key, int seconds) {
        return redisStarter.getUnifiedJedis().expire(key, seconds);
    }

    /**
     * 设置key的过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return 设置成功返回1，失败返回0
     */
    public long expire(byte[] key, int seconds) {
        return redisStarter.getUnifiedJedis().expire(key, seconds);
    }

    /**
     * 获取key的剩余过期时间（秒）
     *
     * @param key 键
     * @return 剩余时间（秒），key不存在返回-2，key存在但没有设置过期时间返回-1
     */
    public long ttl(String key) {
        return redisStarter.getUnifiedJedis().ttl(key);
    }

    /**
     * 获取key的剩余过期时间（秒）
     *
     * @param key 键
     * @return 剩余时间（秒），key不存在返回-2，key存在但没有设置过期时间返回-1
     */
    public long ttl(byte[] key) {
        return redisStarter.getUnifiedJedis().ttl(key);
    }

    /**
     * 获取key的类型
     *
     * @param key 键
     * @return 类型字符串
     */
    public String type(String key) {
        return redisStarter.getUnifiedJedis().type(key);
    }

    /**
     * 获取key的类型
     *
     * @param key 键
     * @return 类型字符串
     */
    public String type(byte[] key) {
        return redisStarter.getUnifiedJedis().type(key);
    }

    /**
     * 修改key的名称
     *
     * @param oldKey 旧键名
     * @param newKey 新键名
     * @return 成功返回OK
     */
    public String rename(String oldKey, String newKey) {
        return redisStarter.getUnifiedJedis().rename(oldKey, newKey);
    }

    /**
     * 修改key的名称
     *
     * @param oldKey 旧键名
     * @param newKey 新键名
     * @return 成功返回OK
     */
    public String rename(byte[] oldKey, byte[] newKey) {
        return redisStarter.getUnifiedJedis().rename(oldKey, newKey);
    }

    /**
     * 仅当newKey不存在时，将oldKey改名为newKey
     *
     * @param oldKey 旧键名
     * @param newKey 新键名
     * @return 成功返回1，失败返回0
     */
    public long renamenx(String oldKey, String newKey) {
        return redisStarter.getUnifiedJedis().renamenx(oldKey, newKey);
    }

    /**
     * 仅当newKey不存在时，将oldKey改名为newKey
     *
     * @param oldKey 旧键名
     * @param newKey 新键名
     * @return 成功返回1，失败返回0
     */
    public long renamenx(byte[] oldKey, byte[] newKey) {
        return redisStarter.getUnifiedJedis().renamenx(oldKey, newKey);
    }

    /**
     * 使用模式匹配获取keys
     *
     * @param pattern 模式，如"user:*"
     * @return 匹配的key集合
     */
    public Set<String> keys(String pattern) {
        return redisStarter.getUnifiedJedis().keys(pattern);
    }

    /**
     * 使用模式匹配获取keys
     *
     * @param pattern 模式，如"user:*"
     * @return 匹配的key集合
     */
    public Set<byte[]> keys(byte[] pattern) {
        return redisStarter.getUnifiedJedis().keys(pattern);
    }

    /**
     * 增量迭代key
     *
     * @param cursor 游标
     * @param params 扫描参数
     * @return 扫描结果
     */
    public ScanResult<String> scan(String cursor, ScanParams params) {
        return redisStarter.getUnifiedJedis().scan(cursor, params);
    }

    /**
     * 增量迭代key
     *
     * @param cursor 游标
     * @param params 扫描参数
     * @return 扫描结果
     */
    public ScanResult<byte[]> scan(byte[] cursor, ScanParams params) {
        return redisStarter.getUnifiedJedis().scan(cursor, params);
    }

    // ============== 字符串操作 ==============

    /**
     * 将key所储存的值加上增量increment
     *
     * @param key       键
     * @param increment 增量值（整数）
     * @return 加上增量后的值
     */
    public long incrBy(String key, long increment) {
        return redisStarter.getUnifiedJedis().incrBy(key, increment);
    }

    /**
     * 将key所储存的值加上增量increment
     *
     * @param key       键
     * @param increment 增量值（整数）
     * @return 加上增量后的值
     */
    public long incrBy(byte[] key, long increment) {
        return redisStarter.getUnifiedJedis().incrBy(key, increment);
    }

    /**
     * 将key所储存的值加1
     *
     * @param key 键
     * @return 加1后的值
     */
    public long incr(String key) {
        return redisStarter.getUnifiedJedis().incr(key);
    }

    /**
     * 将key所储存的值加1
     *
     * @param key 键
     * @return 加1后的值
     */
    public long incr(byte[] key) {
        return redisStarter.getUnifiedJedis().incr(key);
    }

    /**
     * 将key所储存的值减去减量decrement
     *
     * @param key       键
     * @param decrement 减量值（整数）
     * @return 减去减量后的值
     */
    public long decrBy(String key, long decrement) {
        return redisStarter.getUnifiedJedis().decrBy(key, decrement);
    }

    /**
     * 将key所储存的值减去减量decrement
     *
     * @param key       键
     * @param decrement 减量值（整数）
     * @return 减去减量后的值
     */
    public long decrBy(byte[] key, long decrement) {
        return redisStarter.getUnifiedJedis().decrBy(key, decrement);
    }

    /**
     * 将key所储存的值减1
     *
     * @param key 键
     * @return 减1后的值
     */
    public long decr(String key) {
        return redisStarter.getUnifiedJedis().decr(key);
    }

    /**
     * 将key所储存的值减1
     *
     * @param key 键
     * @return 减1后的值
     */
    public long decr(byte[] key) {
        return redisStarter.getUnifiedJedis().decr(key);
    }

    /**
     * 将key所储存的值加上增量increment
     *
     * @param key       键
     * @param increment 增量值（浮点数）
     * @return 加上增量后的值
     */
    public double incrByFloat(String key, double increment) {
        return redisStarter.getUnifiedJedis().incrByFloat(key, increment);
    }

    /**
     * 将key所储存的值加上增量increment
     *
     * @param key       键
     * @param increment 增量值（浮点数）
     * @return 加上增量后的值
     */
    public double incrByFloat(byte[] key, double increment) {
        return redisStarter.getUnifiedJedis().incrByFloat(key, increment);
    }

    /**
     * 追加value到key原来的值的末尾
     *
     * @param key   键
     * @param value 追加的值
     * @return 追加后字符串的长度
     */
    public long append(String key, String value) {
        return redisStarter.getUnifiedJedis().append(key, value);
    }

    /**
     * 追加value到key原来的值的末尾
     *
     * @param key   键
     * @param value 追加的值
     * @return 追加后字符串的长度
     */
    public long append(byte[] key, byte[] value) {
        return redisStarter.getUnifiedJedis().append(key, value);
    }

    /**
     * 获取字符串长度
     *
     * @param key 键
     * @return 字符串长度
     */
    public long strlen(String key) {
        return redisStarter.getUnifiedJedis().strlen(key);
    }

    /**
     * 获取字符串长度
     *
     * @param key 键
     * @return 字符串长度
     */
    public long strlen(byte[] key) {
        return redisStarter.getUnifiedJedis().strlen(key);
    }

    // ============== 哈希操作 ==============

    /**
     * 将哈希表key中的字段field的值设为value
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     * @return 如果字段是新创建的，返回1，如果字段已经存在，返回0
     */
    public long hset(String key, String field, String value) {
        return redisStarter.getUnifiedJedis().hset(key, field, value);
    }

    /**
     * 将哈希表key中的字段field的值设为value
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     * @return 如果字段是新创建的，返回1，如果字段已经存在，返回0
     */
    public long hset(byte[] key, byte[] field, byte[] value) {
        return redisStarter.getUnifiedJedis().hset(key, field, value);
    }

    /**
     * 将哈希表key中的多个字段field的值设为对应的value
     *
     * @param key  键
     * @param hash 字段值映射
     * @return 成功设置的字段数量
     */
    public long hset(String key, Map<String, String> hash) {
        return redisStarter.getUnifiedJedis().hset(key, hash);
    }

    /**
     * 将哈希表key中的多个字段field的值设为对应的value
     *
     * @param key  键
     * @param hash 字段值映射
     * @return 成功设置的字段数量
     */
    public long hset(byte[] key, Map<byte[], byte[]> hash) {
        return redisStarter.getUnifiedJedis().hset(key, hash);
    }

    /**
     * 只有在字段field不存在时，设置哈希表key中字段field的值
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     * @return 设置成功返回1，失败返回0
     */
    public long hsetnx(String key, String field, String value) {
        return redisStarter.getUnifiedJedis().hsetnx(key, field, value);
    }

    /**
     * 只有在字段field不存在时，设置哈希表key中字段field的值
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     * @return 设置成功返回1，失败返回0
     */
    public long hsetnx(byte[] key, byte[] field, byte[] value) {
        return redisStarter.getUnifiedJedis().hsetnx(key, field, value);
    }

    /**
     * 获取哈希表key中字段field的值
     *
     * @param key   键
     * @param field 字段
     * @return 值，字段不存在返回null
     */
    public String hget(String key, String field) {
        return redisStarter.getUnifiedJedis().hget(key, field);
    }

    /**
     * 获取哈希表key中字段field的值
     *
     * @param key   键
     * @param field 字段
     * @return 值，字段不存在返回null
     */
    public byte[] hget(byte[] key, byte[] field) {
        return redisStarter.getUnifiedJedis().hget(key, field);
    }

    /**
     * 获取哈希表key中多个字段field的值
     *
     * @param key    键
     * @param fields 字段数组
     * @return 值列表
     */
    public List<String> hmget(String key, String... fields) {
        return redisStarter.getUnifiedJedis().hmget(key, fields);
    }

    /**
     * 获取哈希表key中多个字段field的值
     *
     * @param key    键
     * @param fields 字段数组
     * @return 值列表
     */
    public List<byte[]> hmget(byte[] key, byte[]... fields) {
        return redisStarter.getUnifiedJedis().hmget(key, fields);
    }

    /**
     * 获取哈希表key中所有字段和值
     *
     * @param key 键
     * @return 字段和值的映射
     */
    public Map<String, String> hgetAll(String key) {
        return redisStarter.getUnifiedJedis().hgetAll(key);
    }

    /**
     * 获取哈希表key中所有字段和值
     *
     * @param key 键
     * @return 字段和值的映射
     */
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        return redisStarter.getUnifiedJedis().hgetAll(key);
    }

    /**
     * 删除哈希表key中的一个或多个字段
     *
     * @param key    键
     * @param fields 字段数组
     * @return 成功删除的字段数量
     */
    public long hdel(String key, String... fields) {
        return redisStarter.getUnifiedJedis().hdel(key, fields);
    }

    /**
     * 删除哈希表key中的一个或多个字段
     *
     * @param key    键
     * @param fields 字段数组
     * @return 成功删除的字段数量
     */
    public long hdel(byte[] key, byte[]... fields) {
        return redisStarter.getUnifiedJedis().hdel(key, fields);
    }

    /**
     * 查看哈希表key中指定字段是否存在
     *
     * @param key   键
     * @param field 字段
     * @return 存在返回true，否则返回false
     */
    public boolean hexists(String key, String field) {
        return redisStarter.getUnifiedJedis().hexists(key, field);
    }

    /**
     * 查看哈希表key中指定字段是否存在
     *
     * @param key   键
     * @param field 字段
     * @return 存在返回true，否则返回false
     */
    public boolean hexists(byte[] key, byte[] field) {
        return redisStarter.getUnifiedJedis().hexists(key, field);
    }

    /**
     * 获取哈希表key中所有字段
     *
     * @param key 键
     * @return 所有字段集合
     */
    public Set<String> hkeys(String key) {
        return redisStarter.getUnifiedJedis().hkeys(key);
    }

    /**
     * 获取哈希表key中所有字段
     *
     * @param key 键
     * @return 所有字段集合
     */
    public Set<byte[]> hkeys(byte[] key) {
        return redisStarter.getUnifiedJedis().hkeys(key);
    }

    /**
     * 获取哈希表key中所有值
     *
     * @param key 键
     * @return 所有值列表
     */
    public List<String> hvals(String key) {
        return redisStarter.getUnifiedJedis().hvals(key);
    }

    /**
     * 获取哈希表key中所有值
     *
     * @param key 键
     * @return 所有值列表
     */
    public List<byte[]> hvals(byte[] key) {
        return redisStarter.getUnifiedJedis().hvals(key);
    }

    /**
     * 获取哈希表key中字段的数量
     *
     * @param key 键
     * @return 字段数量
     */
    public long hlen(String key) {
        return redisStarter.getUnifiedJedis().hlen(key);
    }

    /**
     * 获取哈希表key中字段的数量
     *
     * @param key 键
     * @return 字段数量
     */
    public long hlen(byte[] key) {
        return redisStarter.getUnifiedJedis().hlen(key);
    }

    /**
     * 为哈希表key中的字段field加上增量increment
     *
     * @param key       键
     * @param field     字段
     * @param increment 增量值（整数）
     * @return 加上增量后的值
     */
    public long hincrBy(String key, String field, long increment) {
        return redisStarter.getUnifiedJedis().hincrBy(key, field, increment);
    }

    /**
     * 为哈希表key中的字段field加上增量increment
     *
     * @param key       键
     * @param field     字段
     * @param increment 增量值（整数）
     * @return 加上增量后的值
     */
    public long hincrBy(byte[] key, byte[] field, long increment) {
        return redisStarter.getUnifiedJedis().hincrBy(key, field, increment);
    }

    /**
     * 为哈希表key中的字段field加上增量increment
     *
     * @param key       键
     * @param field     字段
     * @param increment 增量值（浮点数）
     * @return 加上增量后的值
     */
    public double hincrByFloat(String key, String field, double increment) {
        return redisStarter.getUnifiedJedis().hincrByFloat(key, field, increment);
    }

    /**
     * 为哈希表key中的字段field加上增量increment
     *
     * @param key       键
     * @param field     字段
     * @param increment 增量值（浮点数）
     * @return 加上增量后的值
     */
    public double hincrByFloat(byte[] key, byte[] field, double increment) {
        return redisStarter.getUnifiedJedis().hincrByFloat(key, field, increment);
    }

    // ============== 列表操作 ==============

    /**
     * 将一个或多个值value插入到列表key的表头
     *
     * @param key    键
     * @param values 值数组
     * @return 执行LPUSH命令后，列表的长度
     */
    public long lpush(String key, String... values) {
        return redisStarter.getUnifiedJedis().lpush(key, values);
    }

    /**
     * 将一个或多个值value插入到列表key的表头
     *
     * @param key    键
     * @param values 值数组
     * @return 执行LPUSH命令后，列表的长度
     */
    public long lpush(byte[] key, byte[]... values) {
        return redisStarter.getUnifiedJedis().lpush(key, values);
    }

    /**
     * 将一个或多个值value插入到列表key的表尾
     *
     * @param key    键
     * @param values 值数组
     * @return 执行RPUSH命令后，列表的长度
     */
    public long rpush(String key, String... values) {
        return redisStarter.getUnifiedJedis().rpush(key, values);
    }

    /**
     * 将一个或多个值value插入到列表key的表尾
     *
     * @param key    键
     * @param values 值数组
     * @return 执行RPUSH命令后，列表的长度
     */
    public long rpush(byte[] key, byte[]... values) {
        return redisStarter.getUnifiedJedis().rpush(key, values);
    }

    /**
     * 移除并返回列表key的头元素
     *
     * @param key 键
     * @return 列表的头元素，列表为空返回null
     */
    public String lpop(String key) {
        return redisStarter.getUnifiedJedis().lpop(key);
    }

    /**
     * 移除并返回列表key的头元素
     *
     * @param key 键
     * @return 列表的头元素，列表为空返回null
     */
    public byte[] lpop(byte[] key) {
        return redisStarter.getUnifiedJedis().lpop(key);
    }

    /**
     * 移除并返回列表key的尾元素
     *
     * @param key 键
     * @return 列表的尾元素，列表为空返回null
     */
    public String rpop(String key) {
        return redisStarter.getUnifiedJedis().rpop(key);
    }

    /**
     * 移除并返回列表key的尾元素
     *
     * @param key 键
     * @return 列表的尾元素，列表为空返回null
     */
    public byte[] rpop(byte[] key) {
        return redisStarter.getUnifiedJedis().rpop(key);
    }

    /**
     * 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param timeout 等待超时时间（秒）
     * @param key     键
     * @return 第一个元素，超时返回null
     */
    public List<String> blpop(int timeout, String key) {
        return redisStarter.getUnifiedJedis().blpop(timeout, key);
    }

    /**
     * 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param timeout 等待超时时间（秒）
     * @param key     键
     * @return 第一个元素，超时返回null
     */
    public List<byte[]> blpop(int timeout, byte[] key) {
        return redisStarter.getUnifiedJedis().blpop(timeout, key);
    }

    /**
     * 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param timeout 等待超时时间（秒）
     * @param key     键
     * @return 最后一个元素，超时返回null
     */
    public List<String> brpop(int timeout, String key) {
        return redisStarter.getUnifiedJedis().brpop(timeout, key);
    }

    /**
     * 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param timeout 等待超时时间（秒）
     * @param key     键
     * @return 最后一个元素，超时返回null
     */
    public List<byte[]> brpop(int timeout, byte[] key) {
        return redisStarter.getUnifiedJedis().brpop(timeout, key);
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public long llen(String key) {
        return redisStarter.getUnifiedJedis().llen(key);
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public long llen(byte[] key) {
        return redisStarter.getUnifiedJedis().llen(key);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键
     * @param start 开始位置，0表示第一个元素，-1表示最后一个元素
     * @param stop  结束位置
     * @return 指定范围内的元素列表
     */
    public List<String> lrange(String key, long start, long stop) {
        return redisStarter.getUnifiedJedis().lrange(key, start, stop);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键
     * @param start 开始位置，0表示第一个元素，-1表示最后一个元素
     * @param stop  结束位置
     * @return 指定范围内的元素列表
     */
    public List<byte[]> lrange(byte[] key, long start, long stop) {
        return redisStarter.getUnifiedJedis().lrange(key, start, stop);
    }

    /**
     * 根据参数count的值，移除列表中与参数value相等的元素
     *
     * @param key   键
     * @param count 移除数量，>=0从表头开始向表尾搜索，<0从表尾开始向表头搜索，=0移除所有
     * @param value 值
     * @return 被移除元素的数量
     */
    public long lrem(String key, long count, String value) {
        return redisStarter.getUnifiedJedis().lrem(key, count, value);
    }

    /**
     * 根据参数count的值，移除列表中与参数value相等的元素
     *
     * @param key   键
     * @param count 移除数量，>=0从表头开始向表尾搜索，<0从表尾开始向表头搜索，=0移除所有
     * @param value 值
     * @return 被移除元素的数量
     */
    public long lrem(byte[] key, long count, byte[] value) {
        return redisStarter.getUnifiedJedis().lrem(key, count, value);
    }

    /**
     * 对列表进行修剪，只保留指定区间内的元素
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 成功返回OK
     */
    public String ltrim(String key, long start, long stop) {
        return redisStarter.getUnifiedJedis().ltrim(key, start, stop);
    }


    /**
     * 对列表进行修剪，只保留指定区间内的元素
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 成功返回OK
     */
    public String ltrim(byte[] key, long start, long stop) {
        return redisStarter.getUnifiedJedis().ltrim(key, start, stop);
    }

    /**
     * 将列表key下标为index的元素的值设置为value
     *
     * @param key   键
     * @param index 下标
     * @param value 值
     * @return 成功返回OK
     */
    public String lset(String key, long index, String value) {
        return redisStarter.getUnifiedJedis().lset(key, index, value);
    }

    /**
     * 将列表key下标为index的元素的值设置为value
     *
     * @param key   键
     * @param index 下标
     * @param value 值
     * @return 成功返回OK
     */
    public String lset(byte[] key, long index, byte[] value) {
        return redisStarter.getUnifiedJedis().lset(key, index, value);
    }

    /**
     * 返回列表key中，下标为index的元素
     *
     * @param key   键
     * @param index 下标
     * @return 元素值
     */
    public String lindex(String key, long index) {
        return redisStarter.getUnifiedJedis().lindex(key, index);
    }

    /**
     * 返回列表key中，下标为index的元素
     *
     * @param key   键
     * @param index 下标
     * @return 元素值
     */
    public byte[] lindex(byte[] key, long index) {
        return redisStarter.getUnifiedJedis().lindex(key, index);
    }


    // ============== 集合操作 ==============

    /**
     * 将一个或多个member元素加入到集合key中
     *
     * @param key     键
     * @param members 元素数组
     * @return 加入到集合中的新元素的数量
     */
    public long sadd(String key, String... members) {
        return redisStarter.getUnifiedJedis().sadd(key, members);
    }

    /**
     * 将一个或多个member元素加入到集合key中
     *
     * @param key     键
     * @param members 元素数组
     * @return 加入到集合中的新元素的数量
     */
    public long sadd(byte[] key, byte[]... members) {
        return redisStarter.getUnifiedJedis().sadd(key, members);
    }

    /**
     * 移除集合key中的一个或多个member元素
     *
     * @param key     键
     * @param members 元素数组
     * @return 成功移除的元素的数量
     */
    public long srem(String key, String... members) {
        return redisStarter.getUnifiedJedis().srem(key, members);
    }

    /**
     * 移除集合key中的一个或多个member元素
     *
     * @param key     键
     * @param members 元素数组
     * @return 成功移除的元素的数量
     */
    public long srem(byte[] key, byte[]... members) {
        return redisStarter.getUnifiedJedis().srem(key, members);
    }

    /**
     * 返回集合key中的所有成员
     *
     * @param key 键
     * @return 成员集合
     */
    public Set<String> smembers(String key) {
        return redisStarter.getUnifiedJedis().smembers(key);
    }

    /**
     * 返回集合key中的所有成员
     *
     * @param key 键
     * @return 成员集合
     */
    public Set<byte[]> smembers(byte[] key) {
        return redisStarter.getUnifiedJedis().smembers(key);
    }

    /**
     * 判断member元素是否是集合key的成员
     *
     * @param key    键
     * @param member 元素
     * @return 是集合成员返回true，否则返回false
     */
    public boolean sismember(String key, String member) {
        return redisStarter.getUnifiedJedis().sismember(key, member);
    }

    /**
     * 判断member元素是否是集合key的成员
     *
     * @param key    键
     * @param member 元素
     * @return 是集合成员返回true，否则返回false
     */
    public boolean sismember(byte[] key, byte[] member) {
        return redisStarter.getUnifiedJedis().sismember(key, member);
    }

    /**
     * 返回集合key的基数(集合中元素的数量)
     *
     * @param key 键
     * @return 集合的基数
     */
    public long scard(String key) {
        return redisStarter.getUnifiedJedis().scard(key);
    }

    /**
     * 返回集合key的基数(集合中元素的数量)
     *
     * @param key 键
     * @return 集合的基数
     */
    public long scard(byte[] key) {
        return redisStarter.getUnifiedJedis().scard(key);
    }

    /**
     * 随机返回集合中的一个元素
     *
     * @param key 键
     * @return 随机元素
     */
    public String srandmember(String key) {
        return redisStarter.getUnifiedJedis().srandmember(key);
    }

    /**
     * 随机返回集合中的一个元素
     *
     * @param key 键
     * @return 随机元素
     */
    public byte[] srandmember(byte[] key) {
        return redisStarter.getUnifiedJedis().srandmember(key);
    }

    /**
     * 随机返回集合中的count个元素
     *
     * @param key   键
     * @param count 元素数量
     * @return 随机元素列表
     */
    public List<String> srandmember(String key, int count) {
        return redisStarter.getUnifiedJedis().srandmember(key, count);
    }

    /**
     * 随机返回集合中的count个元素
     *
     * @param key   键
     * @param count 元素数量
     * @return 随机元素列表
     */
    public List<byte[]> srandmember(byte[] key, int count) {
        return redisStarter.getUnifiedJedis().srandmember(key, count);
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key 键
     * @return 被移除的随机元素
     */
    public String spop(String key) {
        return redisStarter.getUnifiedJedis().spop(key);
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key 键
     * @return 被移除的随机元素
     */
    public byte[] spop(byte[] key) {
        return redisStarter.getUnifiedJedis().spop(key);
    }

    /**
     * 返回多个集合的交集
     *
     * @param keys 键数组
     * @return 交集成员的集合
     */
    public Set<String> sinter(String... keys) {
        return redisStarter.getUnifiedJedis().sinter(keys);
    }

    /**
     * 返回多个集合的交集
     *
     * @param keys 键数组
     * @return 交集成员的集合
     */
    public Set<byte[]> sinter(byte[]... keys) {
        return redisStarter.getUnifiedJedis().sinter(keys);
    }

    /**
     * 返回多个集合的并集
     *
     * @param keys 键数组
     * @return 并集成员的集合
     */
    public Set<String> sunion(String... keys) {
        return redisStarter.getUnifiedJedis().sunion(keys);
    }

    /**
     * 返回多个集合的并集
     *
     * @param keys 键数组
     * @return 并集成员的集合
     */
    public Set<byte[]> sunion(byte[]... keys) {
        return redisStarter.getUnifiedJedis().sunion(keys);
    }

    /**
     * 返回多个集合的差集
     *
     * @param keys 键数组
     * @return 差集成员的集合
     */
    public Set<String> sdiff(String... keys) {
        return redisStarter.getUnifiedJedis().sdiff(keys);
    }

    /**
     * 返回多个集合的差集
     *
     * @param keys 键数组
     * @return 差集成员的集合
     */
    public Set<byte[]> sdiff(byte[]... keys) {
        return redisStarter.getUnifiedJedis().sdiff(keys);
    }

    // ============== 有序集合操作 ==============

    /**
     * 将一个或多个member元素及其score值加入到有序集key中
     *
     * @param key    键
     * @param score  分数
     * @param member 元素
     * @return 新添加的元素数量
     */
    public long zadd(String key, double score, String member) {
        return redisStarter.getUnifiedJedis().zadd(key, score, member);
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key中
     *
     * @param key    键
     * @param score  分数
     * @param member 元素
     * @return 新添加的元素数量
     */
    public long zadd(byte[] key, double score, byte[] member) {
        return redisStarter.getUnifiedJedis().zadd(key, score, member);
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key中
     *
     * @param key          键
     * @param scoreMembers 分数-元素映射
     * @return 新添加的元素数量
     */
    public long zadd(String key, Map<String, Double> scoreMembers) {
        return redisStarter.getUnifiedJedis().zadd(key, scoreMembers);
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key中
     *
     * @param key          键
     * @param scoreMembers 分数-元素映射
     * @return 新添加的元素数量
     */
    public long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
        return redisStarter.getUnifiedJedis().zadd(key, scoreMembers);
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key中，带参数
     *
     * @param key          键
     * @param scoreMembers 分数-元素映射
     * @param params       参数选项
     * @return 新添加的元素数量
     */
    public long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        return redisStarter.getUnifiedJedis().zadd(key, scoreMembers, params);
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key中，带参数
     *
     * @param key          键
     * @param scoreMembers 分数-元素映射
     * @param params       参数选项
     * @return 新添加的元素数量
     */
    public long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
        return redisStarter.getUnifiedJedis().zadd(key, scoreMembers, params);
    }

    /**
     * 移除有序集key中的一个或多个成员
     *
     * @param key     键
     * @param members 元素数组
     * @return 被成功移除的成员的数量
     */
    public long zrem(String key, String... members) {
        return redisStarter.getUnifiedJedis().zrem(key, members);
    }

    /**
     * 移除有序集key中的一个或多个成员
     *
     * @param key     键
     * @param members 元素数组
     * @return 被成功移除的成员的数量
     */
    public long zrem(byte[] key, byte[]... members) {
        return redisStarter.getUnifiedJedis().zrem(key, members);
    }

    /**
     * 返回有序集key中，指定区间内的成员
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 指定区间内的成员列表
     */
    public List<String> zrange(String key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrange(key, start, stop);
    }

    /**
     * 返回有序集key中，指定区间内的成员
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 指定区间内的成员列表
     */
    public List<byte[]> zrange(byte[] key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrange(key, start, stop);
    }

    /**
     * 返回有序集key中，指定区间内的成员（按score从高到低排序）
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 指定区间内的成员列表
     */
    public List<String> zrevrange(String key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrevrange(key, start, stop);
    }

    /**
     * 返回有序集key中，指定区间内的成员（按score从高到低排序）
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 指定区间内的成员列表
     */
    public List<byte[]> zrevrange(byte[] key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrevrange(key, start, stop);
    }

    /**
     * 返回有序集key中，所有score值介于min和max之间(包括等于min或max)的成员
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 指定分数范围内的成员列表
     */
    public List<String> zrangeByScore(String key, double min, double max) {
        return redisStarter.getUnifiedJedis().zrangeByScore(key, min, max);
    }

    /**
     * 返回有序集key中，所有score值介于min和max之间(包括等于min或max)的成员
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 指定分数范围内的成员列表
     */
    public List<byte[]> zrangeByScore(byte[] key, double min, double max) {
        return redisStarter.getUnifiedJedis().zrangeByScore(key, min, max);
    }

    /**
     * 返回有序集key中，指定区间内的成员及其分数值
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 成员和分数的元组列表
     */
    public List<Tuple> zrangeWithScores(String key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrangeWithScores(key, start, stop);
    }

    /**
     * 返回有序集key中，指定区间内的成员及其分数值
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 成员和分数的元组列表
     */
    public List<Tuple> zrangeWithScores(byte[] key, long start, long stop) {
        return redisStarter.getUnifiedJedis().zrangeWithScores(key, start, stop);
    }

    /**
     * 返回有序集key中member成员的排名（按score从小到大排序）
     *
     * @param key    键
     * @param member 元素
     * @return 排名，从0开始，不存在返回null
     */
    public Long zrank(String key, String member) {
        return redisStarter.getUnifiedJedis().zrank(key, member);
    }

    /**
     * 返回有序集key中member成员的排名（按score从小到大排序）
     *
     * @param key    键
     * @param member 元素
     * @return 排名，从0开始，不存在返回null
     */
    public Long zrank(byte[] key, byte[] member) {
        return redisStarter.getUnifiedJedis().zrank(key, member);
    }

    /**
     * 返回有序集key中member成员的排名（按score从大到小排序）
     *
     * @param key    键
     * @param member 元素
     * @return 排名，从0开始，不存在返回null
     */
    public Long zrevrank(String key, String member) {
        return redisStarter.getUnifiedJedis().zrevrank(key, member);
    }

    /**
     * 返回有序集key中member成员的排名（按score从大到小排序）
     *
     * @param key    键
     * @param member 元素
     * @return 排名，从0开始，不存在返回null
     */
    public Long zrevrank(byte[] key, byte[] member) {
        return redisStarter.getUnifiedJedis().zrevrank(key, member);
    }

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key    键
     * @param member 元素
     * @return 分数值
     */
    public Double zscore(String key, String member) {
        return redisStarter.getUnifiedJedis().zscore(key, member);
    }

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key    键
     * @param member 元素
     * @return 分数值
     */
    public Double zscore(byte[] key, byte[] member) {
        return redisStarter.getUnifiedJedis().zscore(key, member);
    }

    /**
     * 返回有序集key的基数
     *
     * @param key 键
     * @return 有序集的基数
     */
    public long zcard(String key) {
        return redisStarter.getUnifiedJedis().zcard(key);
    }

    /**
     * 返回有序集key的基数
     *
     * @param key 键
     * @return 有序集的基数
     */
    public long zcard(byte[] key) {
        return redisStarter.getUnifiedJedis().zcard(key);
    }

    /**
     * 返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员的数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 分数范围内的成员数量
     */
    public long zcount(String key, double min, double max) {
        return redisStarter.getUnifiedJedis().zcount(key, min, max);
    }

    /**
     * 返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员的数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 分数范围内的成员数量
     */
    public long zcount(byte[] key, double min, double max) {
        return redisStarter.getUnifiedJedis().zcount(key, min, max);
    }

    /**
     * 为有序集key的成员member的score值加上增量increment
     *
     * @param key       键
     * @param member    元素
     * @param increment 增量值
     * @return 加上增量后的分数值
     */
    public double zincrby(String key, double increment, String member) {
        return redisStarter.getUnifiedJedis().zincrby(key, increment, member);
    }

    /**
     * 为有序集key的成员member的score值加上增量increment
     *
     * @param key       键
     * @param member    元素
     * @param increment 增量值
     * @return 加上增量后的分数值
     */
    public double zincrby(byte[] key, double increment, byte[] member) {
        return redisStarter.getUnifiedJedis().zincrby(key, increment, member);
    }

    /**
     * 返回服务端信息
     *
     * @return
     */
    public String info() {
        byte[] info = (byte[]) redisStarter.getUnifiedJedis().sendCommand(Protocol.Command.INFO);
        return new String(info);
    }

    /**
     * 返回库大小
     *
     * @return
     */
    public long dbSize() {
        return redisStarter.getUnifiedJedis().dbSize();
    }
    // ============== 通用方法 ==============

    /**
     * 获取原始客户端，用于执行特定操作
     *
     * @return UnifiedJedis客户端
     */
    public UnifiedJedis getClient() {
        return redisStarter.getUnifiedJedis();
    }
}
