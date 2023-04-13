package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SubscribeType {
    DATA("DATA", "数据上报"),
    EVENT("EVENT", "事件上报"),;

    private String value;
    private String text;

    SubscribeType(String value, String text) {
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

    public static SubscribeType from(String value) {
        for (SubscribeType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SubscribeType: " + value);
    }
}
