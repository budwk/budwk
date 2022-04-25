package com.budwk.app.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 消息发送范围
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysMsgScope {
    ALL("ALL", "全部用户"),
    SCOPE("SCOPE", "指定用户");

    private String value;
    private String text;

    SysMsgScope(String value, String text) {
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

    public static SysMsgScope from(String value) {
        for (SysMsgScope t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysMsgScope: " + value);
    }

}
