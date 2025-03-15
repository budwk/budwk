package com.budwk.starter.wechat.at.impl;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import com.budwk.starter.wechat.at.WxAccessToken;
import com.budwk.starter.wechat.spi.WxAccessTokenStore;

public class MemoryAccessTokenStore implements WxAccessTokenStore {

    private static final Log log = Logs.get();

    WxAccessToken at;

    @Override
    public WxAccessToken get() {
        return at;
    }

    @Override
    public void save(String token, int time, long lastCacheTimeMillis) {
        at = new WxAccessToken();
        at.setToken(token);
        at.setExpires(time);
        at.setLastCacheTimeMillis(lastCacheTimeMillis);
        log.debugf("new wx access token generated : \n %s", Json.toJson(at, JsonFormat.nice()));
    }

}
