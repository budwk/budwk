package com.budwk.starter.wechat.at.impl;

import com.budwk.starter.wechat.at.WxJsapiTicket;
import com.budwk.starter.wechat.spi.WxJsapiTicketStore;

public class MemoryJsapiTicketStore implements WxJsapiTicketStore {

    WxJsapiTicket jt;

    @Override
    public WxJsapiTicket get() {
        return jt;
    }

    @Override
    public void save(String ticket, int expires, long lastCacheTimeMillis) {
        jt = new WxJsapiTicket(ticket, expires, lastCacheTimeMillis);
    }

}
