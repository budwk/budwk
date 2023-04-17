package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 规则执行方式
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum RuleAction {
    NOTICE("NOTICE", "通知"),
    URL("URL", "URL转发"),
    PUSH("PUSH", "队列推送"),
    CMD("CMD", "指令下发");

    private String value;
    private String text;

    RuleAction(String value, String text) {
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

    public static RuleAction from(String value) {
        for (RuleAction t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown RuleAction: " + value);
    }
}
