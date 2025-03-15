package com.budwk.starter.wechat.bean;

/**
 * 代金券
 * Created by wizzer on 2017/3/24.
 */
public class WxPayCoupon {
    private String coupon_stock_id;
    private int  openid_count;
    private String partner_trade_no;
    private String openid;
    private String appid;
    private String mch_id;
    private String op_user_id;
    private String device_info;
    private String nonce_str;
    private String sign;
    private String version;
    private String type;

    public String getCoupon_stock_id() {
        return coupon_stock_id;
    }

    public void setCoupon_stock_id(String coupon_stock_id) {
        this.coupon_stock_id = coupon_stock_id;
    }

    public int getOpenid_count() {
        return openid_count;
    }

    public void setOpenid_count(int openid_count) {
        this.openid_count = openid_count;
    }

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getOp_user_id() {
        return op_user_id;
    }

    public void setOp_user_id(String op_user_id) {
        this.op_user_id = op_user_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
