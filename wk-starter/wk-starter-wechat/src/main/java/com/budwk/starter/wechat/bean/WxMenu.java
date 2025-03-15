package com.budwk.starter.wechat.bean;

import java.util.List;

import org.nutz.json.JsonField;

public class WxMenu {

    private String name;
    private String type;
    private String key;
    private String url;
    private String appid;
    private String pagepath;
    private String media_id;

    @JsonField("sub_button")
    private List<WxMenu> subButtons;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPagepath() {
        return pagepath;
    }

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public List<WxMenu> getSubButtons() {
        return subButtons;
    }

    public void setSubButtons(List<WxMenu> subButtons) {
        this.subButtons = subButtons;
    }
}
