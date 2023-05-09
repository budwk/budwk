package com.budwk.app.device.cache;

import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.DeviceProcessCache;
import org.nutz.lang.util.NutMap;

/**
 * 设备缓存
 * @author wizzer.cn
 */
public interface DeviceCaching {
    boolean cacheDeviceMix(Device_info deviceInfo,
                           Device_product product,
                           NutMap subDeviceInfo);

    boolean updateDeviceInfo(String deviceId, DeviceInfoUpdateModel model);

    /**
     * 该方法会更新所有的缓存信息为提供的值。谨慎调用
     *
     * @param deviceProcessCache 修改后的缓存信息
     * @return
     */
    boolean updateAll(DeviceProcessCache deviceProcessCache);

    <T> boolean updateDeviceField(String deviceId, String fieldName, T value);

    DeviceProcessCache getDeviceCache(String deviceId);

    boolean deleteDeviceInfo(String deviceId);

    boolean setEx(String key,String value,int expire);

    String get(String key);

    boolean del(String key);

    boolean hset(String key,String field,String value);
}
