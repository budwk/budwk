package com.budwk.starter.wechat.bean;

import org.nutz.json.JsonField;

public class WxKfAccount {

    @JsonField("kf_account")
    String account;
    
    @JsonField("kf_headimgurl")
    String headimgurl;
    
    @JsonField("kf_id")
    String id;
    
    @JsonField("kf_nick")
    String nick;
    
    
    int status;
    
    int auto_accept;
    
    int accepted_case;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuto_accept() {
        return auto_accept;
    }

    public void setAuto_accept(int auto_accept) {
        this.auto_accept = auto_accept;
    }

    public int getAccepted_case() {
        return accepted_case;
    }

    public void setAccepted_case(int accepted_case) {
        this.accepted_case = accepted_case;
    }
}
