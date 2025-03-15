package com.budwk.starter.wechat.spi;

import com.budwk.starter.wechat.at.WxJsapiTicket;

public interface WxJsapiTicketStore {

	WxJsapiTicket get();

	void save(String ticket, int expires, long lastCacheTimeMillis);

}
