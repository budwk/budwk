package com.budwk.starter.wechat.at.impl;

import com.budwk.starter.wechat.at.WxAccessToken;
import com.budwk.starter.wechat.spi.WxAccessTokenStore;

public abstract class CacheableAccessTokenStore implements WxAccessTokenStore {

    protected int timeout;
    protected WxAccessToken at;
    protected Object lock = new Object();

    @Override
    public WxAccessToken get() {
        if (timeout > 0) {
            synchronized (lock) {
                if (timeout > 0
                    && at != null
                    && (System.currentTimeMillis() / 1000 - at.getExpires()) < timeout)
                    return at;
            }
        }
        WxAccessToken tmp = _getAccessToken();
        if (timeout > 0) {
            synchronized (lock) {
                at = tmp;
            }
        }
        return tmp;
    }

    protected abstract WxAccessToken _getAccessToken();

    protected abstract void _saveAccessToken(String token, int time);

    @Override
    public void save(String token, int time, long lastCacheTimeMillis) {
        _saveAccessToken(token, time);
        if (time > 0) {
            synchronized (lock) {
                at = null;
                get();
            }
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
