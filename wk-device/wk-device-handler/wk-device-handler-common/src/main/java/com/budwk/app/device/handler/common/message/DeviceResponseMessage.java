package com.budwk.app.device.handler.common.message;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 回复消息
 *
 * @author wizzer.cn
 */
@Data
public class DeviceResponseMessage implements DeviceMessage {
    private static final long serialVersionUID = -3546777340652167412L;
    private String messageType = "response";
    /**
     * 回复的消息ID
     */
    private String messageId;
    /**
     * 回复的指令方法
     */
    private String commandCode;
    private String deviceId;
    /**
     * 指令是否成功执行
     */
    private boolean success;
    /**
     * 指令响应的属性，注意：值不要使用协议包中的自定义数据结构，需要转成通用的集合、数组、Map等
     */
    private final Map<String, Object> properties = new LinkedHashMap<>();
    /**
     * 指令响应的时间
     */
    private long timestamp;

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

    public DeviceResponseMessage addProperty(String code, Object value) {
        properties.put(code, value);
        return this;
    }

    public Object getProperty(String code) {
        return properties.get(code);
    }
}
