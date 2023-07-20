package com.budwk.app.device.handler.common.codec.context;

import java.util.Map;

/**
 * 设备事件
 * @author wizzer.cn
 */
public interface DeviceEventContext {
    /**
     * 更新设备字段
     *
     * @param deviceId
     * @param key
     * @param value
     */
    void updateDeviceField(String deviceId, String key, Object value);

    /**
     * 更新设备字段
     *
     * @param deviceId
     * @param kv
     */
    void updateDeviceField(String deviceId, Map<String, Object> kv);
}
