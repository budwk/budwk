package com.budwk.starter.sms.enums;

/**
 * @author wizzer@qq.com
 */
public enum SmsType {
    /**
     * 注册短信验证码
     */
    REGISTER("register"),
    /**
     * 登录短信验证码
     */
    LOGIN("login"),
    /**
     * 找回密码验证码
     */
    PASSWORD("password");

    private String value;

    private SmsType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
