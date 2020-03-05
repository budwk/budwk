package com.budwk.nb.web.controllers.open.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.wx.services.WxConfigService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.Xmls;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer(wizzer@qq.com) on 2016/7/3.
 */
@IocBean
@At("/open/pay/wx/back")
public class WxPayBackController {
    private static final Log log = Logs.get();
    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;

    @At
    @Ok("raw")
    @AdaptBy(type = VoidAdaptor.class)
    public String sendNotify(Reader reader) throws IOException {
        NutMap res = Xmls.xmlToMap(Streams.readAndClose(reader));
        log.debug("res::" + Json.toJson(res));
        Map<String, Object> map = new HashMap<>();
        // FAIL = 失败
        map.put("return_code", "SUCCESS");
        return Xmls.mapToXml(map);
    }

}
