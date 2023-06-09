package com.budwk.app.wx.controllers.open;

import com.budwk.app.wx.commons.service.WxPay3Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
@Slf4j
@At("/wechat/open/wxpay")
public class WxPay3NotifyController {
    @Inject
    private WxPay3Service wxPay3Service;
    private String mchid;

    public void init() {
        mchid = ""; //商户号:可从配置文件或者 wx_pay 表里获取
    }

    @At("/notify")
    @Ok("json")
    public Object notify(HttpServletRequest req, HttpServletResponse response) {
        NutMap map = NutMap.NEW();
        try {
            String timestamp = req.getHeader("Wechatpay-Timestamp");
            String nonce = req.getHeader("Wechatpay-Nonce");
            String serialNo = req.getHeader("Wechatpay-Serial");
            String signature = req.getHeader("Wechatpay-Signature");
            String body = Lang.readAll(req.getReader());
            log.info("timestamp:" + timestamp + " nonce:" + nonce + " serialNo:" + serialNo + " signature:" + signature);
            String result = wxPay3Service.verifyNotify(mchid, serialNo, body, signature, nonce, timestamp);
            if (Strings.isNotBlank(result)) {
                log.info("解密后的明文:{}", result);
                NutMap data = Json.fromJson(NutMap.class, result);
                //交易号
                String out_trade_no = data.getString("out_trade_no");
                String trade_state = data.getString("trade_state");
                //业务代码

                map.put("code", "SUCCESS");
                map.put("message", "成功");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return map;
    }
}
