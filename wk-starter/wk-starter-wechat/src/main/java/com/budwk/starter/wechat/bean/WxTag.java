package com.budwk.starter.wechat.bean;

import org.nutz.json.JsonIgnore;

public class WxTag {

    private Integer id;
    private String name;
    @JsonIgnore(null_double = 0)
    private Integer count;

    public WxTag() {
        super();
    }

    public WxTag(Integer id) {
        super();
        this.id = id;
    }

    public WxTag(String name) {
        super();
        this.name = name;
    }

    public WxTag(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "WxTagPojo [id=" + id + ", name=" + name + ", count=" + count + "]";
    }

}
