package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * AEP http协议所需信息（前后端约定内容，不用枚举了）
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
@Deprecated
public enum AuthAepHttp {
    masterKey("masterKey", "AEP产品masterKey"),
    productId("productId", "AEP产品ID");

    private String value;
    private String text;

    AuthAepHttp(String value, String text) {
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

    public static AuthAepHttp from(String value) {
        for (AuthAepHttp t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown AuthAepHttp: " + value);
    }
}
