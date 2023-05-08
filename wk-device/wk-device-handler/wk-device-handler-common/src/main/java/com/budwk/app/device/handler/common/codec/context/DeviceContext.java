package com.budwk.app.device.handler.common.codec.context;

import java.util.Map;

/**
 * 设备字段更新上下文
 * @author wizzer.cn
 */
public interface DeviceContext {
    /**
     * 更新设备字段
     *
     * @param deviceId   设备ID
     * @param fieldName  字段名
     * @param fieldValue 字段值
     */
    void update(String deviceId, String fieldName, Object fieldValue);

    /**
     * 更新设备字段
     *
     * @param deviceId 设备ID
     * @param kv 字段名-字段值对象
     */
    void update(String deviceId, Map<String, Object> kv);
}
