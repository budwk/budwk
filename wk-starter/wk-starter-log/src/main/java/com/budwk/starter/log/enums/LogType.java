package com.budwk.starter.log.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum LogType {
    SYSTEM("SYSTEM", "系统日志"),
    LOGIN("LOGIN", "登录日志"),
    ACTION("ACTION", "业务日志");

    private String value;
    private String text;

    LogType(String value, String text) {
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

    public static LogType from(String value) {
        for (LogType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown LogType: " + value);
    }
}
