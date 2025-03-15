package com.budwk.starter.wechat.impl;

import com.budwk.starter.wechat.session.memory.MemorySessionManager;
import com.budwk.starter.wechat.spi.WxApi2;
import com.budwk.starter.wechat.spi.WxSessionManager;

public abstract class AbstractLightWx extends AbstractWxHandler {

    protected WxApi2 api;
    
    protected WxSessionManager sessionManager;
    
    public AbstractLightWx() {
        api = new WxApi2Impl();
        sessionManager = new MemorySessionManager();
    }
    
}
