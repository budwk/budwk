package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 设备在线状态
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum DeviceOnline {
    NOTACTIVE("NOTACTIVE", "未激活"),
    ONLINE("ONLINE", "在线"),
    OFFLINE("OFFLINE", "离线");

    private String value;
    private String text;

    DeviceOnline(String value, String text) {
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

    public static DeviceOnline from(String value) {
        for (DeviceOnline t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown DeviceOnline: " + value);
    }
}
