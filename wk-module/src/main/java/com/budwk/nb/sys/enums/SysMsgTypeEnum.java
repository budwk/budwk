package com.budwk.nb.sys.enums;

/**
 * 系统消息类型
 * Created by wizzer.cn on 2019/12/17
 */
public enum SysMsgTypeEnum {

    API("system", "enums.sys.msg.type.system"),
    PLATFORM("user", "enums.sys.msg.type.user");

    private String key;
    private String value;

    SysMsgTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String value() {
        return key;
    }

    public String text() {
        return value;
    }

    public static SysMsgTypeEnum from(String key) {
        for (SysMsgTypeEnum ft : SysMsgTypeEnum.values()) {
            if (key.equals(ft.key)) {
                return ft;
            }
        }
        throw new IllegalArgumentException("unknown SysMsgTypeEnum: " + key);
    }

}
