package com.budwk.starter.wechat.spi;

import com.budwk.starter.wechat.at.WxAccessToken;

public interface WxAccessTokenStore {

	WxAccessToken get();

	void save(String token, int expires, long lastCacheTimeMillis);
}
