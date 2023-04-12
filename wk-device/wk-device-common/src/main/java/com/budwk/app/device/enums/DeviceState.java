package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum DeviceState {
    NORMAL("NORMAL", "正常"),
    ABNORMAL("ABNORMAL", "异常");

    private String value;
    private String text;

    DeviceState(String value, String text) {
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

    public static DeviceState from(String value) {
        for (DeviceState t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown DeviceState: " + value);
    }
}
