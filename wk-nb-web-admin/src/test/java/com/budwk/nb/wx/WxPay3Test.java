package com.budwk.nb.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.utils.DateUtil;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.core.WebMainLauncher;
import com.budwk.nb.web.commons.ext.wx.WxPay3Service;
import com.budwk.nb.wx.models.Wx_pay;
import com.budwk.nb.wx.services.WxMinaService;
import com.budwk.nb.wx.services.WxPayService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.boot.NbApp;
import org.nutz.boot.test.junit4.NbJUnit4Runner;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.weixin.bean.WxPay3Response;
import org.nutz.weixin.util.WxPay3Api;
import org.nutz.weixin.util.WxPay3Util;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wizzer@qq.com
 */
@IocBean
@RunWith(NbJUnit4Runner.class)
public class WxPay3Test extends Assert {
    @Inject
    @Reference
    private WxPayService wxPayService;
    @Inject
    @Reference
    private WxMinaService wxMinaService;
    @Inject
    private WxPay3Service wxPay3Service;
    public static Wx_pay wxPay;
    public static String mchid = "xxx";
    public static String appid = "xxx";
    public static final SimpleDateFormat DATE_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public Wx_pay getWxPay() {
        if (wxPay == null) {
            return wxPayService.fetch(Cnd.where("mchid", "=", mchid));
        } else return wxPay;
    }

    @Test
    public void changeTime() throws ParseException {
        Date date1 = new Date();
        System.out.println(Times.format(DATE_TIME_ZONE, date1));
        String time = "2020-10-20T16:44:25+08:00";
        Date date = Times.parse(DATE_TIME_ZONE, time);
        System.out.println(date.getTime());
    }

    @Test
    public void test_v3_getKeyFile() {
        String keyString = new String(Files.readBytes(getWxPay().getV3keyPath()));
        System.out.println("keyString:::" + keyString);
        System.out.println("keyString:::" + StandardCharsets.UTF_8.name());
    }

    @Test
    public void test_v3_getSerialNo() {
        String serialNo = WxPay3Util.getCertSerialNo(getWxPay().getV3certPath());
        System.out.println("serialNo:::" + serialNo);
    }

    @Test
    public void test_v3_getPlatformCertificates() throws Exception {
        String serialNo = WxPay3Util.getCertSerialNo(getWxPay().getV3certPath());
        WxPay3Response wxPay3Response = WxPay3Api.v3_certificates(mchid, serialNo, getWxPay().getV3keyPath());
        System.out.println("certificates:::" + Json.toJson(wxPay3Response));
    }

    @Test
    public void test_v3_order() throws Exception {
        String orderPayNo = R.UU32();
        String orderId = R.UU32();
        NutMap wxPayUnifiedOrder = NutMap.NEW();
        wxPayUnifiedOrder.addv("appid", appid);
        wxPayUnifiedOrder.addv("mchid", mchid);
        wxPayUnifiedOrder.addv("description", new String(("LaiShop-order-" + orderId).getBytes(), StandardCharsets.UTF_8));
        wxPayUnifiedOrder.addv("out_trade_no", orderPayNo);
        Date now = new Date();
        wxPayUnifiedOrder.addv("time_expire", DateUtil.getDateAfterMinute(now, 30));
        wxPayUnifiedOrder.addv("notify_url", Globals.AppDomain + "/shop/open/wxpay/" + mchid + "/notify");
        wxPayUnifiedOrder.addv("amount", NutMap.NEW().addv("total", 1).addv("currency", "CNY"));
        wxPayUnifiedOrder.addv("payer", NutMap.NEW().addv("openid", "o9Bnd4lXKfNsOci-6H98zCMWyBps"));
        String body = Json.toJson(wxPayUnifiedOrder);
        System.out.println("body::" + body);
        WxPay3Response wxPay3Response = wxPay3Service.v3_order_jsapi(mchid, body);
        System.out.println("wxPay3Response::" + Json.toJson(wxPay3Response));
        boolean verifySignature = wxPay3Service.verifySignature(wxPay3Response, mchid);
        System.out.println("verifySignature::" + verifySignature);
        NutMap v3order = Json.fromJson(NutMap.class, wxPay3Response.getBody());
        NutMap resp = wxPay3Service.v3_call_jsapi(mchid, appid, v3order.getString("prepay_id"));
        System.out.println("resp::" + Json.toJson(resp));
    }

    // 测试类可提供public的static的createNbApp方法,用于定制当前测试类所需要的NbApp对象.
    // 测试类带@IocBean或不带@IocBean,本规则一样生效
    // 若不提供,默认使用当前测试类作为MainLauncher.
    // 也可以自定义NbJUnit4Runner, 继承NbJUnit4Runner并覆盖其createNbApp方法
    public static NbApp createNbApp() {
        NbApp nb = new NbApp().setMainClass(WebMainLauncher.class).setPrintProcDoc(false);
        nb.getAppContext().setMainPackage("com.budwk");
        return nb;
    }
}
