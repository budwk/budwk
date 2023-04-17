package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 指令状态
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum CommandStatus {
    WAIT("WAIT", "待下发"),
    SEND("SEND", "已下发"),
    DELIVERED("DELIVERED", "已送达"),
    FINISHED("FINISHED", "已完成"),
    ERROR("ERROR", "下发失败"),
    FAILED("FAILED", "执行失败"),
    CANCELED("CANCELED", "已取消"),
    TIMEOUT("TIMEOUT", "执行超时"),
    EXPIRED("EXPIRED", "过期失效");

    private String value;
    private String text;

    CommandStatus(String value, String text) {
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

    public static CommandStatus from(String value) {
        for (CommandStatus t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown CommandStatus: " + value);
    }
}
