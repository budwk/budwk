package com.budwk.starter.wechat.at.impl;

import com.budwk.starter.wechat.at.WxCardTicket;
import com.budwk.starter.wechat.spi.WxCardTicketStore;

/**
 * 
 * @author JinYi(wdhlzd@163.com)
 *
 */
public class MemoryCardTicketStore implements WxCardTicketStore {

    WxCardTicket ct;

    @Override
    public WxCardTicket get() {
        return ct;
    }

    @Override
    public void save(String ticket, int expires, long lastCacheTimeMillis) {
    	ct = new WxCardTicket(ticket, expires, lastCacheTimeMillis);
    }

}
