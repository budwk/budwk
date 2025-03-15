package com.budwk.starter.wechat.session;

import com.budwk.starter.wechat.bean.WxInMsg;
import com.budwk.starter.wechat.spi.WxSession;
import com.budwk.starter.wechat.spi.WxSessionManager;

public abstract class AbstractWxSessionManager implements WxSessionManager {

    public WxSession getSession(String id) {
        return getSession(id, true);
    }

    public WxSession getSession(WxInMsg msg) {
        return getSession(msg, true);
    }

    public WxSession getSession(WxInMsg msg, boolean create) {
        return getSession(msg.getFromUserName(), create);
    }


}
