package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 产品字段类型
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum FieldType {
    PROP("PROP", "设备属性"),
    ATTR("ATTR", "设备参数");

    private String value;
    private String text;

    FieldType(String value, String text) {
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

    public static FieldType from(String value) {
        for (FieldType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown FieldType: " + value);
    }
}
