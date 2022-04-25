package com.budwk.app.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统消息类型
 * JsonShape注解用于Json和实体类的相互转换,必须加
 *
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysUnitType {
    GROUP("GROUP", "总公司"),
    COMPANY("COMPANY", "分公司"),
    UNIT("UNIT", "部门");

    private String value;
    private String text;

    SysUnitType(String value, String text) {
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

    public static SysUnitType from(String value) {
        for (SysUnitType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysUnitType: " + value);
    }


}
