package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 设备数据类型
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum DataType {
    INTEGER("INTEGER", "整型"),
    FLOAT("FLOAT", "浮点型"),
    STRING("STRING", "字符串"),
    ENUM("ENUM", "枚举"),
    TIMESTAMP("TIMESTAMP", "时间戳"),
    DATETIME("DATETIME", "日期时间"),
    IP("IP", "IP地址");

    private String value;
    private String text;

    DataType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public String value() {
        return value;
    }

    public String text() {
        return text;
    }

    public static DataType from(String value) {
        for (DataType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown DataType: " + value);
    }
}
