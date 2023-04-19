package com.budwk.app.device.storage.enums;

import org.nutz.json.JsonShape;

/**
 * @author wizzer.cn
 */
@JsonShape(JsonShape.Type.OBJECT)
public enum TimeSeriesGranularity {
    SECONDS("SECONDS","秒"),
    MINUTES("MINUTES","分钟"),
    HOURS("HOURS","小时");

    private String value;
    private String text;

    TimeSeriesGranularity(String value, String text) {
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

    public static TimeSeriesGranularity from(String value) {
        for (TimeSeriesGranularity t : values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown TimeSeriesGranularity: " + value);
    }
}
