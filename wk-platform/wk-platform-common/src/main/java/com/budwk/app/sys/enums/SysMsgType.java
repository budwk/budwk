package com.budwk.app.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统消息类型
 * JsonShape注解用于Json和实体类的相互转换,必须加
 *
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysMsgType {
    SYSTEM("SYSTEM", "系统消息"),
    USER("USER", "用户消息");

    private String value;
    private String text;

    SysMsgType(String value, String text) {
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

    public static SysMsgType from(String value) {
        for (SysMsgType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysMsgType: " + value);
    }


}
