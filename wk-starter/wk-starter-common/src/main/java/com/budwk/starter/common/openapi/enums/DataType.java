package com.budwk.starter.common.openapi.enums;

/**
 * @author wizzer@qq.com
 */
public enum DataType {
    OBJECT("object"),
    ARRAY("array");

    private String value;

    private DataType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
