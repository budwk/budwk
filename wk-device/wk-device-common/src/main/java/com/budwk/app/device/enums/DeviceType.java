package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 设备分类（用于扩展开发固定业务功能）
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum DeviceType {
    METER("METER", "表具"),
    ALARM("ALARM", "报警器"),
    COLLECTOR("COLLECTOR", "集中器"),
    VALVE("VALVE", "远控阀门"),
    PRESSURE("PRESSURE", "压力监测"),
    CAMARA("CAMARA", "摄像头"),
    HOME("HOME", "家居"),
    OTHER("OTHER", "其他");

    private String value;
    private String text;

    DeviceType(String value, String text) {
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

    public static DeviceType from(String value) {
        for (DeviceType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown DeviceType: " + value);
    }
}
