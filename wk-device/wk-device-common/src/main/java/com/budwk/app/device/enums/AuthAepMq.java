package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * AEP MQ协议所需信息（前后端约定内容，不用枚举了）
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
@Deprecated
public enum AuthAepMq {
    productId("productId", "AEP产品ID");

    private String value;
    private String text;

    AuthAepMq(String value, String text) {
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

    public static AuthAepMq from(String value) {
        for (AuthAepMq t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown AuthAepMq: " + value);
    }
}
