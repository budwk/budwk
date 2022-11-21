package com.budwk.app.wx.controllers.open;

import com.budwk.app.wx.commons.service.WxHandler;
import com.budwk.app.wx.services.WxConfigService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.weixin.util.Wxs;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author wizzer(wizzer.cn) on 2016/7/3.
 */
@IocBean
@At("/open/weixin")
public class WechatMsgController {
    private static final Log log = Logs.get();
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxHandler wxHandler;

    public WechatMsgController() {
        Wxs.enableDevMode(); // 开启debug模式,这样就会把接收和发送的内容统统打印,方便查看
    }

    @At("/msg/{key}")
    @Fail("http:200")
    @AdaptBy(type = WhaleAdaptor.class)
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(wxHandler, req, key);
    }

}
