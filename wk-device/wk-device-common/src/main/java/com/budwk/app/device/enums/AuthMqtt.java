package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * mqtt协议所需信息（前后端约定内容，不用枚举了）
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
@Deprecated
public enum AuthMqtt {
    username("username", "用户名"),
    password("password", "密码");

    private String value;
    private String text;

    AuthMqtt(String value, String text) {
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

    public static AuthMqtt from(String value) {
        for (AuthMqtt t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown AuthMqtt: " + value);
    }
}
