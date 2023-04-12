package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum ParamType {
    INDEX("INDEX", "指标"),
    VALVE("VALVE", "阀门"),
    STATE("STATE", "状态"),
    INFO("INFO", "信息"),
    OTHER("OTHER", "其他");

    private String value;
    private String text;

    ParamType(String value, String text) {
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

    public static ParamType from(String value) {
        for (ParamType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown DevicePropertyType: " + value);
    }
}
