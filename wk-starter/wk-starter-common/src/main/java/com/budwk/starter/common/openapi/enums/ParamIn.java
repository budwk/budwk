package com.budwk.starter.common.openapi.enums;

/**
 * @author wizzer@qq.com
 */
public enum ParamIn {
    DEFAULT(""),
    HEADER("header"),
    QUERY("query"),
    PATH("path"),
    COOKIE("cookie");

    private String value;

    private ParamIn(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
