package com.budwk.starter.wechat.at.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import com.budwk.starter.wechat.at.WxAccessToken;
import com.budwk.starter.wechat.spi.WxAccessTokenStore;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 中央缓存access_token 底层为redis的实现 需要引入jedis客户端支持 参考IOC配置： customerRedisTokenStore
 * : { type : "com.budwk.starter.wechat.at.impl.RedisTokenStore", fields : { tokenKey :
 * 'customer_token', jedisPool : { refer : 'jedisPool' } } }, jedisPool : { type
 * : "redis.clients.jedis.JedisPool", args : [{refer : 'poolConfig'},
 * '127.0.0.1', 6379] } }, poolConfig : { type :
 * "org.apache.commons.pool2.impl.GenericObjectPoolConfig", fields : {
 * testWhileIdle : false, maxTotal : 200, maxIdle : 10, maxWaitMillis : 10000,
 * testOnBorrow : true, testOnReturn : true } }
 *
 * @author JiangKun
 * @date 2016年12月14日 下午5:04:08
 */
public class RedisAccessTokenStore implements WxAccessTokenStore {

    private static final Log log = Logs.get();

    protected String tokenKey = "wxmp:access_token";

    protected JedisPool jedisPool;

    public RedisAccessTokenStore() {
    }

    public RedisAccessTokenStore(String tokenKey, JedisPool jedisPool) {
        if (!Strings.isBlank(tokenKey))
            this.tokenKey = tokenKey;
        this.jedisPool = jedisPool;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    @Override
    public WxAccessToken get() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (tokenKey == null) {
                throw new RuntimeException("Redis token key should not be null!");
            }
            Map<String, String> hash = jedis.hgetAll(tokenKey);
            if (Lang.isEmpty(hash)) {
                log.warnf("could not find a valid token in redis with key [%s]", tokenKey);
                return null;
            }
            WxAccessToken at = new WxAccessToken();// 从redis中拿出3个值组装成WxAccessToken返回
            at.setToken(hash.get("token"));
            at.setLastCacheTimeMillis(Long.valueOf(hash.get("lastCacheMillis")));
            at.setExpires(Integer.valueOf(hash.get("expires")));
            log.debugf("wx access_token fetched from redis with the key [%s] : \n %s",
                    tokenKey,
                    Json.toJson(at, JsonFormat.nice()));
            return at;
        } catch (Exception e) {
            log.error(e);
        } finally {
            // jedisPool.returnResource(jedis); //这是老版本归还连接的方法 已经deprecated
            jedis.close();// 2.9.0的方法直接close
        }
        return null;
    }

    @Override
    public void save(String token, int expires, long lastCacheTimeMillis) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (tokenKey == null) {
                throw new RuntimeException("Redis access_token key should not be null!");
            }
            Map<String, String> hash = new HashMap<String, String>();
            hash.put("token", token);// 存入token值
            hash.put("lastCacheMillis", String.valueOf(lastCacheTimeMillis));// 存入设置的过期时间
            hash.put("expires", String.valueOf(expires));// 存入当前缓存时间
            String result = jedis.hmset(tokenKey, hash);
            log.infof("A new wx access_token was generated and stored to redis with the key [%s] , redis return code : %s",
                    tokenKey,
                    result);
        } catch (Exception e) {
            log.error(e);
        } finally {
            jedis.close();
        }
    }
}
