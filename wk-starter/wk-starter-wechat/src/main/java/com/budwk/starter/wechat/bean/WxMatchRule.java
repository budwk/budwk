package com.budwk.starter.wechat.bean;

public class WxMatchRule {

    String tag_id;
    String sex;
    String country;
    String province;
    String city;
    String client_platform_type;
    String language;

    public WxMatchRule() {}

    public WxMatchRule(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public WxMatchRule setTag_id(String tag_id) {
        this.tag_id = tag_id;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public WxMatchRule setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public WxMatchRule setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public WxMatchRule setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCity() {
        return city;
    }

    public WxMatchRule setCity(String city) {
        this.city = city;
        return this;
    }

    public String getClient_platform_type() {
        return client_platform_type;
    }

    public WxMatchRule setClient_platform_type(String client_platform_type) {
        this.client_platform_type = client_platform_type;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public WxMatchRule setLanguage(String language) {
        this.language = language;
        return this;
    }

}
