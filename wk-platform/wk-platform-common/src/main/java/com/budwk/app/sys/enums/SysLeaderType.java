package com.budwk.app.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统消息类型
 * JsonShape注解用于Json和实体类的相互转换,必须加
 *
 * @author wizzer@qq.com
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysLeaderType {
    LEADER("LEADER", "单位主管"),
    HIGHER("HIGHER", "上级主管领导"),
    ASSIGNER("ASSIGNER", "上级分管领导");

    private String value;
    private String text;

    SysLeaderType(String value, String text) {
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

    public static SysLeaderType from(String value) {
        for (SysLeaderType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysUnitType: " + value);
    }


}
