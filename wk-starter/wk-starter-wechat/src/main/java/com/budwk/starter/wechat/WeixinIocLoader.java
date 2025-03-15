package com.budwk.starter.wechat;

import org.nutz.ioc.loader.json.JsonLoader;

public class WeixinIocLoader extends JsonLoader {

    public WeixinIocLoader() {
        super("com/budwk/starter/wechat/wechat.js");
    }
}
