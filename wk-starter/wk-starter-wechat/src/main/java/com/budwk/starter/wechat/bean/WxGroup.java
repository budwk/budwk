package com.budwk.starter.wechat.bean;

import org.nutz.json.JsonIgnore;

public class WxGroup {

    @JsonIgnore(null_double = 0)
    private int id;
    private String name;
    @JsonIgnore(null_double = 0)
    private int count;

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected int getCount() {
        return count;
    }

    protected void setCount(int count) {
        this.count = count;
    }
}
