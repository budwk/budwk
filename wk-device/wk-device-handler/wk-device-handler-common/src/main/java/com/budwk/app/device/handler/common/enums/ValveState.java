package com.budwk.app.device.handler.common.enums;

/**
 * @author wizzer.cn
 */
public enum ValveState {
    STATE_0(0, "无阀门"),
    STATE_1(1, "阀门开"),
    STATE_2(2, "阀门临时关（用户可手动打开）"),
    STATE_3(3, "阀门强制关（用户无法手动打开）"),
    STATE_4(4, "阀门状态未知"),
    STATE_5(5, "阀门异常"),
    STATE_6(6, "开关阀中");


    private final int value;
    private final String text;

    ValveState(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int value() {
        return value;
    }

    public String text() {
        return text;
    }

    public static ValveState from(int value) {
        if (value < 0 || value > 5) {
            return STATE_4;
        }
        return values()[value];

    }
}
