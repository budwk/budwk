package com.budwk.app.device.handler.common.message;

import com.budwk.app.device.handler.common.device.ValueItem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件消息
 * @author zyang
 * @author wizzer.cn
 */
@Getter
@Setter
public class DeviceEventMessage implements DeviceMessage {
    private static final long serialVersionUID = 4481831480000516793L;
    private String messageType = "event";
    private String messageId;
    private String deviceId;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 事件内容
     */
    private String content;
    /**
     * 发生告警时对应的告警值
     */
    private String warningValue;
    /**
     * 事件的属性，注意：值不要使用协议包中的自定义数据结构，需要转成通用的集合、数组、Map等
     */
    private final List<ValueItem<? extends Serializable>> properties = new ArrayList<>();
    /**
     * 数据上报的时间
     */
    private long timestamp;

    private Type type;

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public long getTimestamp() {
        return timestamp == 0 ? System.currentTimeMillis() : timestamp;
    }

    public DeviceEventMessage addProperty(String code, String name, Serializable value) {
        properties.add(new ValueItem<>(code, name, value));
        return this;
    }

    public enum Type {
        /**
         * 信息
         */
        INFO,
        /**
         * 告警
         */
        ALARM,
        /**
         * 告警恢复
         */
        ALARM_RECOVER
    }
}
