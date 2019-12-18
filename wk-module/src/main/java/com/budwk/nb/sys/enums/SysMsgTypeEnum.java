package com.budwk.nb.sys.enums;

import org.nutz.json.JsonShape;

/**
 * 系统消息类型
 * Created by wizzer.cn on 2019/12/17
 */
@JsonShape(JsonShape.Type.OBJECT) //此注解用于Json和实体类的相互转换,必须加
public enum SysMsgTypeEnum {

    SYSTEM("system", "enums.sys.msg.type.system"),
    USER("user", "enums.sys.msg.type.user");

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
