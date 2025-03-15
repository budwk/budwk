package com.budwk.starter.wechat.bean;

import java.util.Collection;
import java.util.Map;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;

import org.nutz.lang.Strings;

public class WxTemplateData {

    public static final String DFT_COLOR = "#173177";

    private String value;
    private String color = DFT_COLOR;

    public WxTemplateData() {}

    public WxTemplateData(Object obj) {
        // 空
        if (null == obj) {
            this.set(null, null);
        }
        // 如果就是 map 则将其转换，假想必须有 value 这个键
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            Object value = map.get("value");
            Object color = map.get("color");
            this.set(value, color);
        }
        // 数组 [value, color?]
        // 集合 [value, color?]
        else if (obj.getClass().isArray() || obj instanceof Collection<?>) {
            final Object[] vv = new Object[2];
            Lang.each(obj, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    if (index >= 2)
                        Lang.Break();
                    vv[index] = ele;
                }
            });
            this.set(vv[0], vv[1]);
        }
        // 其他，转成字符串
        else {
            this.set(obj, null);
        }
    }

    public WxTemplateData(String value, String color) {
        set(value, color);
    }

    public void set(Object value, Object color) {
        this.value = Strings.sBlank(value);
        this.color = Strings.sBlank(color);

        // if (Strings.isBlank(this.value)) {
        // //throw Lang.makeThrow("blank value");
        // }

        if (Strings.isBlank(this.color)) {
            this.color = DFT_COLOR;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
