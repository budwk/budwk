package com.budwk.nb.app.controllers.open;

import com.budwk.nb.app.commons.ext.wx.WxPay3Service;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/shop/open/wxpay")
@Ok("json")
@OpenAPIDefinition(tags = {@Tag(name = "商城_微信支付回调")}, servers = @Server(url = "/"))
public class WxPay3NotifyController {
    private static final Log log = Logs.get();
    @Inject
    private WxPay3Service wxPay3Service;

    @At("/{mchid}/notify")
    @Ok("raw")
    @AdaptBy(type = VoidAdaptor.class)
    public void notify(String mchid, Reader reader, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            NutMap map = NutMap.NEW();
            String timestamp = req.getHeader("Wechatpay-Timestamp");
            String nonce = req.getHeader("Wechatpay-Nonce");
            String serialNo = req.getHeader("Wechatpay-Serial");
            String signature = req.getHeader("Wechatpay-Signature");
            log.debugf("timestamp=%s,nonce=%s,serialNo=%s,signature=%s", timestamp, nonce, serialNo, signature);
            String body = Streams.readAndClose(reader);
            // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
            String plainText = wxPay3Service.verifyNotify(mchid, serialNo, body, signature, nonce, timestamp);
            log.debugf("支付通知明文=%s", plainText);
            NutMap res = Json.fromJson(NutMap.class, plainText);
            NutMap payer = res.getAs("payer", NutMap.class);
            String trade_state = res.getString("trade_state");
            String out_trade_no = res.getString("out_trade_no");
            String openid = payer.getString("openid");
            boolean isOk = true;//业务代码
            if ("SUCCESS".equals(trade_state) && isOk) {
                resp.setStatus(200);
                map.put("code", "SUCCESS");
                map.put("message", "SUCCESS");
            } else {
                resp.setStatus(500);
                map.put("code", "ERROR");
                map.put("message", "签名错误");
            }
            resp.setHeader("Content-type", "application/json");
            resp.getOutputStream().write(Json.toJson(map).getBytes(StandardCharsets.UTF_8));
            resp.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
