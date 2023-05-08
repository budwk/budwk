package com.budwk.app.device.handler.common.codec;

import com.budwk.app.device.handler.common.device.CommandInfo;

/**
 * 资源装载类
 * @author wizzer.cn
 */
public interface DeviceRegistry {
    /**
     * 通过指定字段获取网关设备
     * 如果是网关设备，调用这个接口获取
     *
     * @param field 字段名。仅支持 collectorNo、id 这两个字段
     * @param value 字段值
     * @return
     */
    DeviceOperator getGatewayDevice(String field, String value);

    /**
     * 通过指定字段获取设备
     *
     * @param field 字段名
     * @param value 值
     * @return
     */
    DeviceOperator getDeviceOperator(String field, String value);

    /**
     * 通过设备id获取设备
     *
     * @param deviceId 设备id
     * @return
     */
    DeviceOperator getDeviceOperator(String deviceId);

    CommandInfo getDeviceCommand(String deviceId);

    void updateDeviceOnline(String deviceId);

    DeviceOperator getLastData(String deviceId, DeviceOperator operator);
}
