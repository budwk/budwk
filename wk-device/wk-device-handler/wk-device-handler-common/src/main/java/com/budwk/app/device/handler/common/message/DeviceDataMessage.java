package com.budwk.app.device.handler.common.message;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 上报消息
 *
 * @author wizzer.cn
 */
@Getter
@Setter
public class DeviceDataMessage implements DeviceMessage {
    private static final long serialVersionUID = -5920683171058683792L;
    private String messageType = "report";
    private String messageId;
    private String deviceId;
    /**
     * 上报的属性，注意：值不要使用协议包中的自定义数据结构，需要转成通用的集合、数组、Map等
     */
    private Map<String, Object> properties = new LinkedHashMap<>();
    /**
     * 数据上报的时间
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

    public DeviceDataMessage addProperty(String code, Object value) {
        properties.put(code, value);
        return this;
    }

    public Object getProperty(String code) {
        return properties.get(code);
    }
}
