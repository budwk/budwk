package com.budwk.starter.websocket;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean
public class WebSockertStarter {

    protected static final String PRE = "websocket.";

    @PropDoc(value = "websocket 运行模式", defaultValue = "redis")
    public static final String PROP_WEBSOCKET_TYPE = PRE + "type";


}
