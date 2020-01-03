package com.budwk.nb.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统消息类型
 * @author wizzer(wizzer@qq.com) on 2019/12/17
 * JsonShape注解用于Json和实体类的相互转换,必须加
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum SysMsgTypeEnum {
    /**
     * system
     */
    SYSTEM("SYSTEM", "enums.sys.msg.type.system"),
    /**
     * user
     */
    USER("USER", "enums.sys.msg.type.user");

    private String value;
    private String text;

    SysMsgTypeEnum(String value, String text) {
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

    public static SysMsgTypeEnum from(String value) {
        for (SysMsgTypeEnum t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown SysMsgTypeEnum: " + value);
    }


}
