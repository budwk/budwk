package com.budwk.app.device.enums;

import org.nutz.json.JsonShape;

/**
 * 接入平台
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum IotPlatform {
    AEP("AEP", "中国电信AEP平台"),
    ONENET("ONENET", "中国移动物联网开放平台"),
    OTHER("OTHER", "其他");

    private String value;
    private String text;

    IotPlatform(String value, String text) {
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

    public static IotPlatform from(String value) {
        for (IotPlatform t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown IotPlatform: " + value);
    }
}
