package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;
import org.nutz.lang.util.NutMap;

/**
 * 阀门状态
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum ValveState {
    NONE(-2, "无阀门"),
    UNKNOWN(-1, "未知"),
    ON(0, "开启"),
    OFF(1, "关闭"),
    TEMPORARY_ON(2, "临时开"),
    TEMPORARY_OFF(3, "临时关"),
    FORCE_ON(4, "强制开"),
    FORCE_OFF(5, "强制关"),
    FAULT(6,"故障");

    private int value;
    private String text;

    ValveState(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static ValveState from(int value) {
        for (ValveState c : ValveState.values()) {
            if (value == c.value) {
                return c;
            }
        }
        return UNKNOWN;
    }

    public static NutMap toMap() {
        NutMap map = NutMap.NEW();
        for (ValveState v : ValveState.values()) {
            map.put(String.valueOf(v.getValue()), v.getText());
        }
        return map;
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
