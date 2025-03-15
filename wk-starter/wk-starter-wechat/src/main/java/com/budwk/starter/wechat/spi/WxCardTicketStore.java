package com.budwk.starter.wechat.spi;

import com.budwk.starter.wechat.at.WxCardTicket;

public interface WxCardTicketStore {

	WxCardTicket get();

	void save(String ticket, int expires, long lastCacheTimeMillis);

}
