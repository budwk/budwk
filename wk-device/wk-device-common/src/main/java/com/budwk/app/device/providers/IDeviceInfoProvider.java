package com.budwk.app.device.providers;

import com.budwk.app.device.enums.DeviceOnline;
import com.budwk.app.device.enums.DeviceState;
import com.budwk.app.device.enums.DeviceValveState;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_product;
import org.nutz.dao.Cnd;

/**
 * @author wizzer.cn
 */
public interface IDeviceInfoProvider {
    /**
     * 获取设备所属产品
     *
     * @param productId 产品ID
     * @return
     */
    Device_product getProduct(String productId);

    /**
     * 获取设备信息
     *
     * @param cnd 条件表达式
     * @return
     */
    Device_info getDevice(Cnd cnd);

    /**
     * 更新设备状态
     *
     * @param deviceId 设备ID
     * @param online   在线状态
     */
    void updateDeviceOnline(String deviceId, DeviceOnline online);

    /**
     * 更新设备状态
     *
     * @param deviceId 设备ID
     * @param state    设备状态
     */
    void updateDeviceState(String deviceId, DeviceState state);

    /**
     * @param deviceId 设备ID
     * @param state    设备状态
     * @param online   在线状态
     */
    void updateDeviceState(String deviceId, DeviceState state, DeviceOnline online);

    /**
     * 更新表具设备阀门状态
     *
     * @param deviceId 设备ID
     * @param state    阀门状态
     */
    void updateMeterValveState(String deviceId, DeviceValveState state);

    void updateIgnoreNull(Device_info deviceInfo);
}
