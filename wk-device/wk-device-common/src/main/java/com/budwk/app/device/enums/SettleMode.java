package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SettleMode {
    PREPAID(0, "预付费"),
    POSTPAID(1, "后付费");

    private int value;
    private String text;

    SettleMode(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static SettleMode from(int value) {
        for (SettleMode c : SettleMode.values()) {
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
