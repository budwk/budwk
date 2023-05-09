package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum PayMode {
    METER(0, "表端计费"),
    PLATFORM(1, "平台计费");

    private int value;
    private String text;

    PayMode(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static PayMode from(int value) {
        for (PayMode c : PayMode.values()) {
            if (value == c.value) {
                return c;
            }
        }
        return null;
    }

    public int value() {
        return value;
    }

    public String text() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
