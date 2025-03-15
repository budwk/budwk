package com.budwk.starter.wechat.spi;

import org.nutz.lang.util.NutMap;
import com.budwk.starter.wechat.bean.*;

import java.io.File;
import java.util.Map;

/**
 * Created by wizzer on 2017/3/23.
 */
public interface WxPayApi {
    NutMap postPay(String url, String key, Map<String, Object> params);

    NutMap postPay(String url, String key, Map<String, Object> params, Object keydata, String password);

    NutMap pay_unifiedorder(String key, WxPayUnifiedOrder wxPayUnifiedOrder);

    NutMap pay_jsapi(String key, WxPayUnifiedOrder wxPayUnifiedOrder);

    NutMap pay_transfers(String key, WxPayTransfers wxPayTransfers, Object keydata, String password);

    NutMap send_redpack(String key, WxPayRedPack wxRedPack, Object keydata, String password);

    NutMap send_redpackgroup(String key, WxPayRedPackGroup wxRedPackGroup, Object keydata, String password);

    NutMap send_coupon(String key, WxPayCoupon wxPayCoupon, Object keydata, String password);

    NutMap pay_refund(String key, WxPayRefund wxPayRefund, Object keydata, String password);

    NutMap pay_refundquery(String key, WxPayRefundQuery wxPayRefundQuery);
    
    // 兼容老方法
    NutMap pay_transfers(String key, WxPayTransfers wxPayTransfers, File keydata, String password);

    NutMap send_redpack(String key, WxPayRedPack wxRedPack, File keydata, String password);

    NutMap send_redpackgroup(String key, WxPayRedPackGroup wxRedPackGroup, File keydata, String password);

    NutMap send_coupon(String key, WxPayCoupon wxPayCoupon, File keydata, String password);

    NutMap pay_refund(String key, WxPayRefund wxPayRefund, File keydata, String password);
}
