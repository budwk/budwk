package com.budwk.app.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统参数类型
 *
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysConfigType {
    TEXT("TEXT", "文本"),
    IMAGE("IMAGE", "图片"),
    BOOL("BOOL", "布尔值");

    private String value;
    private String text;

    SysConfigType(String value, String text) {
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

    public static SysConfigType from(String value) {
        for (SysConfigType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysConfigType: " + value);
    }


}

