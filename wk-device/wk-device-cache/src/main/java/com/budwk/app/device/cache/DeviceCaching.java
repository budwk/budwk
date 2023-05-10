package com.budwk.app.device.cache;

import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_info_meter;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.DeviceInfoUpdateModel;
import com.budwk.app.device.objects.cache.DeviceProcessCache;
import org.nutz.lang.util.NutMap;

/**
 * 设备缓存
 *
 * @author wizzer.cn
 */
public interface DeviceCaching {
    boolean cache(Device_info deviceInfo,
                  Device_info_meter deviceInfoMeter,
                  Device_product product,
                  NutMap subDeviceInfo);

    /**
     * 该方法会更新所有的缓存信息为提供的值。谨慎调用
     *
     * @param deviceProcessCache 修改后的缓存信息
     * @return true/false
     */
    boolean update(DeviceProcessCache deviceProcessCache);

    /**
     * 更新设备部分字段
     *
     * @param deviceId  设备ID
     * @param fieldName 设备字段名
     * @param value     字段值
     * @return true/false
     */
    <T> boolean updateDeviceField(String deviceId, String fieldName, T value);

    /**
     * 更新设备信息
     *
     * @param deviceId 设备ID
     * @param model    DeviceInfoUpdateModel
     * @return true/false
     */
    boolean updateDeviceInfo(String deviceId, DeviceInfoUpdateModel model);

    DeviceProcessCache getDeviceCache(String deviceId);

    boolean deleteDeviceCache(String deviceId);
}
