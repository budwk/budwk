package com.budwk.app.cms.enums;

import org.nutz.json.JsonShape;

/**
 * 栏目类型
 * @author wizzer(wizzer.cn) on 2020/02/28
 * JsonShape注解用于Json和实体类的相互转换,必须加
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum CmsChannelType {
    ARTICLE("ARTICLE", "文章"),
    PHOTO("PHOTO", "相册");

    private String value;
    private String text;

    CmsChannelType(String value, String text) {
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

    public static CmsChannelType from(String value) {
        for (CmsChannelType t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown CmsChannelType: " + value);
    }


}
