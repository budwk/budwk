package com.budwk.starter.wechat.util;

import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import com.budwk.starter.wechat.bean.WxPay3Response;

/**
 * 微信支付V3 API请求类
 * 参考项目 https://github.com/Javen205/IJPay
 * 实例详见 https://github.com/budwk/budwk-nutzboot
 *
 * @author wizzer@qq.com
 */
public class WxPay3Api {
    public static String PAY_DOMAIN = "https://api.mch.weixin.qq.com";

    /**
     * 通过V3 API证书和商户号获取平台证书
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_certificates(String mchId, String serialNo, String keyPath) throws Exception {
        String url = "/v3/certificates";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("GET", url, mchId, serialNo, null, keyPath, "", nonceStr, timestamp, authType);
    }

    /**
     * JSAPI/小程序下单
     * body json 参数详见 https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pay/transactions/chapter3_2.shtml
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @param body     传参
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_jsapi(String mchId, String serialNo, String keyPath, String body) throws Exception {
        String url = "/v3/pay/transactions/jsapi";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * APP下单
     * body json 参数详见 https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pay/transactions/chapter3_1.shtml
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @param body     传参
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_app(String mchId, String serialNo, String keyPath, String body) throws Exception {
        String url = "/v3/pay/transactions/app";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * Native下单
     * body json 参数详见 https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pay/transactions/chapter3_3.shtml
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @param body     传参
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_native(String mchId, String serialNo, String keyPath, String body) throws Exception {
        String url = "/v3/pay/transactions/native";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * H5下单
     * body json 参数详见 https://pay.weixin.qq.com/wiki/doc/apiv3/wxpay/pay/transactions/chapter3_4.shtml
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @param body     传参
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_h5(String mchId, String serialNo, String keyPath, String body) throws Exception {
        String url = "/v3/pay/transactions/h5";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * 关闭订单
     *
     * @param mchId        商户号
     * @param serialNo     V3API证书序列号
     * @param keyPath      V3Key证书文件路径
     * @param out_trade_no 商户订单号
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_close(String mchId, String serialNo, String keyPath, String out_trade_no) throws Exception {
        String url = "/v3/pay/transactions/out-trade-no/" + out_trade_no + "/close";
        long timestamp = System.currentTimeMillis() / 1000;
        String body = Json.toJson(NutMap.NEW().addv("mchid", mchId));
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * 微信支付订单号查询
     *
     * @param mchId          商户号
     * @param serialNo       V3API证书序列号
     * @param keyPath        V3Key证书文件路径
     * @param transaction_id 微信支付订单号
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_query_transaction_id(String mchId, String serialNo, String keyPath, String transaction_id) throws Exception {
        String url = "/v3/pay/transactions/id/" + transaction_id;
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("GET", url, mchId, serialNo, null, keyPath, "", nonceStr, timestamp, authType);
    }

    /**
     * 商户订单号查询
     *
     * @param mchId        商户号
     * @param serialNo     V3API证书序列号
     * @param keyPath      V3Key证书文件路径
     * @param out_trade_no 商户订单号
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_order_query_out_trade_no(String mchId, String serialNo, String keyPath, String out_trade_no) throws Exception {
        String url = "/v3/pay/transactions/out_trade_no/" + out_trade_no + "?mchid=" + mchId;
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("GET", url, mchId, serialNo, null, keyPath, "", nonceStr, timestamp, authType);
    }

    /**
     * 申请退款
     *
     * @param mchId    商户号
     * @param serialNo V3API证书序列号
     * @param keyPath  V3Key证书文件路径
     * @param body     传参
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_ecommerce_refunds_apply(String mchId, String serialNo, String keyPath, String body) throws Exception {
        String url = "/v3/ecommerce/refunds/apply";
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("POST", url, mchId, serialNo, null, keyPath, body, nonceStr, timestamp, authType);
    }

    /**
     * 退款查询
     *
     * @param mchId     商户号
     * @param serialNo  V3API证书序列号
     * @param keyPath   V3Key证书文件路径
     * @param refund_id 微信退款ID
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_ecommerce_refunds_query_refund_id(String mchId, String serialNo, String keyPath, String refund_id) throws Exception {
        String url = "/v3/ecommerce/refunds/id/" + refund_id + "?sub_mchid=" + mchId;
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("GET", url, mchId, serialNo, null, keyPath, "", nonceStr, timestamp, authType);
    }

    /**
     * 退款查询
     *
     * @param mchId     商户号
     * @param serialNo  V3API证书序列号
     * @param keyPath   V3Key证书文件路径
     * @param out_refund_no 微信退款单号
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_ecommerce_refunds_query_out_refund_no(String mchId, String serialNo, String keyPath, String out_refund_no) throws Exception {
        String url = "/v3/ecommerce/refunds/out-refund-no/" + out_refund_no + "?sub_mchid=" + mchId;
        long timestamp = System.currentTimeMillis() / 1000;
        String authType = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = R.UU32().toUpperCase();
        return v3_call("GET", url, mchId, serialNo, null, keyPath, "", nonceStr, timestamp, authType);
    }

    /**
     * 发起http请求
     *
     * @param method       请求方法
     * @param url          URL后缀
     * @param mchId        商户号
     * @param serialNo     V3API序列号
     * @param platSerialNo 平台证书序列号
     * @param keyPath      V3Key证书文件路径
     * @param body         传参
     * @param nonceStr     随机字符串
     * @param timestamp    时间戳
     * @param authType     认证类型
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_call(String method, String url, String mchId, String serialNo, String platSerialNo, String keyPath,
                                         String body, String nonceStr, long timestamp, String authType) throws Exception {
        String authorization = WxPay3Util.buildAuthorization(method, url, mchId, serialNo,
                keyPath, body, nonceStr, timestamp, authType);
        if (Strings.isBlank(platSerialNo)) {
            platSerialNo = serialNo;
        }
        if ("GET".equals(method)) {
            return v3_call_get(PAY_DOMAIN + url, authorization, platSerialNo);
        }
        if ("POST".equals(method)) {
            return v3_call_post(PAY_DOMAIN + url, authorization, platSerialNo, body);
        }
        return null;
    }

    /**
     * get请求
     *
     * @param url
     * @param authorization
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_call_get(String url, String authorization, String serialNumber) throws Exception {
        Request req = Request.create(url, Request.METHOD.GET);
        req.getHeader().addAll(WxPay3Util.getHeaders(authorization, serialNumber));
        Sender sender = Sender.create(req);
        Response resp = sender.send();
        if (!resp.isOK())
            throw new IllegalStateException("resp code=" + resp.getStatus());
        WxPay3Response wxPay3Response = new WxPay3Response();
        wxPay3Response.setBody(resp.getContent("UTF-8"));
        wxPay3Response.setHeader(resp.getHeader());
        wxPay3Response.setStatus(resp.getStatus());
        return wxPay3Response;
    }

    /**
     * post请求
     *
     * @param url
     * @param authorization
     * @param serialNumber
     * @param body
     * @return
     * @throws Exception
     */
    public static WxPay3Response v3_call_post(String url, String authorization, String serialNumber, String body) throws Exception {
        Request req = Request.create(url, Request.METHOD.POST);
        req.setData(body);
        req.getHeader().addAll(WxPay3Util.getHeaders(authorization, serialNumber));
        Sender sender = Sender.create(req);
        Response resp = sender.send();
        if (!resp.isOK())
            throw new IllegalStateException("resp code=" + resp.getStatus());
        WxPay3Response wxPay3Response = new WxPay3Response();
        wxPay3Response.setBody(resp.getContent("UTF-8"));
        wxPay3Response.setHeader(resp.getHeader());
        wxPay3Response.setStatus(resp.getStatus());
        return wxPay3Response;
    }
}
