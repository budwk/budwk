package com.budwk.starter.wechat.bean;

/**
 * 普通红包
 * Created by wizzer on 2017/3/23.
 */
public class WxPayRedPack {
    private String nonce_str;
    private String sign;
    private String mch_billno;
    private String mch_id;
    private String wxappid;
    private String send_name;
    private String re_openid;
    private int total_amount;
    private int total_num;
    private String wishing;
    private String client_ip;
    private String act_name;
    private String remark;
    private String scene_id;
    private String risk_info;
    private String consume_mch_id;

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

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getWxappid() {
        return wxappid;
    }

    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getRisk_info() {
        return risk_info;
    }

    public void setRisk_info(String risk_info) {
        this.risk_info = risk_info;
    }

    public String getConsume_mch_id() {
        return consume_mch_id;
    }

    public void setConsume_mch_id(String consume_mch_id) {
        this.consume_mch_id = consume_mch_id;
    }
}
