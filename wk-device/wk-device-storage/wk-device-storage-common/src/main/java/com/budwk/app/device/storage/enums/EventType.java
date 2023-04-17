package com.budwk.app.device.storage.enums;

import org.nutz.json.JsonShape;

/**
 * 设备事件类型
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum EventType {
    INFO("INFO", "信息"),
    WARNING("WARNING", "告警");

    private String value;
    private String text;

    EventType(String value, String text) {
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

    public static EventType from(String value) {
        for (EventType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown EventType: " + value);
    }
}
