package com.budwk.app.device.handler.common.codec.impl;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.device.ProductInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 缺省设备操作
 * @author wizzer.cn
 */
@Data
public class DefaultDeviceOperator implements DeviceOperator {
    private String deviceId;
    private String sessionId;
    private String gateway;
    private boolean online;
    private long onlineTime;
    private long offlineTime;
    private int waitCmdCount;

    /**
     * 产品信息
     */
    private ProductInfo productInfo;

    private Map<String, Object> properties;
    private Map<String, Object> updatedProperties;

    public void setOnline(boolean online) {
        this.online = online;
        if (online) {
            this.onlineTime = System.currentTimeMillis();
        } else {
            this.offlineTime = System.currentTimeMillis();
        }
    }


    @Override
    public Object getProperty(String key) {
        if (null == properties) {
            return null;
        }
        return properties.get(key);
    }

    @Override
    public <T> T getProperty(Class<T> type, String key) {
        Object property = getProperty(key);
        return null == property ? null : (T) property;
    }

    @Override
    public void setProperty(String key, Object value) {
        if (null == properties) {
            properties = new HashMap<>();
        }
        if (null == updatedProperties) {
            updatedProperties = new HashMap<>();
        }
        properties.put(key, value);
        updatedProperties.put(key, value);
    }

    @Override
    public ProductInfo getProduct() {
        return productInfo;
    }
}
