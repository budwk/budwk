package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 指令类型
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum CommandType {
    HEX("HEX", "16进制透传"),
    JSON("JSON", "参数配置");

    private String value;
    private String text;

    CommandType(String value, String text) {
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

    public static CommandType from(String value) {
        for (CommandType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown CommandType: " + value);
    }
}
