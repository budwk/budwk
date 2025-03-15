package com.budwk.starter.wechat.at.impl;

import com.budwk.starter.wechat.at.WxAccessToken;
import com.budwk.starter.wechat.spi.WxAccessTokenStore;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import redis.clients.jedis.UnifiedJedis;

import java.util.HashMap;
import java.util.Map;

public class JedisAgenAccessTokenStore implements WxAccessTokenStore {

    protected String tokenKey = "wxmp:access_token";

    protected UnifiedJedis unifiedJedis;

    public JedisAgenAccessTokenStore(String tokenKey, UnifiedJedis unifiedJedis) {
        if (!Strings.isBlank(tokenKey))
            this.tokenKey = tokenKey;
        this.unifiedJedis = unifiedJedis;
    }

    public WxAccessToken get() {
        Map<String, String> map = unifiedJedis.hgetAll(tokenKey);
        if (map == null || map.isEmpty())
            return null;
        return Lang.map2Object(map, WxAccessToken.class);
    }

    public void save(String token, int expires, long lastCacheTimeMillis) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("expires", "" + expires);
        map.put("lastCacheTimeMillis", "" + lastCacheTimeMillis);
        unifiedJedis.hmset(tokenKey, map);
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }
}
