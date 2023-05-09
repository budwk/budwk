package com.budwk.app.device.handler.demo.meter.enums;

/**
 * @author wizzer.cn
 */
public enum AlarmType {
    ALARM_0(0, "","其他"),
    ALARM_1(1,"pressure", "压力高报警"),
    ALARM_2(2,"pressure", "压力低报警");

    private final int value;
    private final String propKey;
    private final String text;

    AlarmType(int value, String propKey, String text) {
        this.value = value;
        this.propKey = propKey;
        this.text = text;
    }

    public int value() {
        return value;
    }
    public String text() {
        return text;
    }
    public String propKey() {
        return propKey;
    }

    public static AlarmType from(int value) {
        if (value < 1 || value > 18) {
            return ALARM_0;
        }
        return values()[value];

    }
}
