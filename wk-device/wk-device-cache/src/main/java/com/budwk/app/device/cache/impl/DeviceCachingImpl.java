package com.budwk.app.device.cache.impl;

import com.budwk.app.device.cache.DeviceCaching;
import com.budwk.app.device.cache.constants.CacheConstant;
import com.budwk.app.device.enums.DeviceType;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_info_meter;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.DeviceInfoUpdateModel;
import com.budwk.app.device.objects.cache.DeviceProcessCache;
import com.budwk.starter.redisjson.RedisJsonService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wizzer.cn
 */
@IocBean
public class DeviceCachingImpl implements DeviceCaching {

    @Inject
    private RedisJsonService redisJsonService;

    Mirror<DeviceProcessCache> mirror = Mirror.me(DeviceProcessCache.class);
    Map<String, Class<?>> fields = Arrays.stream(mirror.getFields()).collect(Collectors.toMap(Field::getName, Field::getType));

    @Override
    public boolean cache(Device_info deviceInfo,
                         Device_info_meter deviceInfoMeter,
                         Device_product product,
                         NutMap subDeviceInfo) {
        DeviceProcessCache cache = new DeviceProcessCache();
        cache.setDeviceId(deviceInfo.getId());
        cache.setDeviceNo(deviceInfo.getDeviceNo());
        cache.setIccid(deviceInfo.getIccid());
        if (null != subDeviceInfo) {
            subDeviceInfo.forEach((k, v) -> {
                if (null == v) {
                    return;
                }
                Class<?> fT = fields.get(k);
                if (null != fT && v.getClass().isAssignableFrom(fT)) {
                    mirror.setValue(cache, k, v);
                }
            });
        }
        cache.setProductId(product.getId());
        cache.setSupplierId(product.getSupplierId());
        cache.setDeviceType(product.getDeviceType());
        // 表具类型设备
        if (product.getDeviceType() == DeviceType.METER && deviceInfoMeter != null) {
            cache.setLastReadNumber(deviceInfoMeter.getReadNumber());
            cache.setLastReceiveTime(deviceInfoMeter.getReceiveTime());
            cache.setLastReadTime(deviceInfoMeter.getReadTime());
            cache.setLastDeviceValveState(deviceInfoMeter.getDeviceValveState());
        }
        String ok = redisJsonService.setObject(CacheConstant.DEVICE_CACHE_KEY.concat(deviceInfo.getId()), cache);
        return ok.equals(CacheConstant.SUCCESS);
    }

    @Override
    public boolean update(DeviceProcessCache deviceInfo) {
        String ok = redisJsonService.setObject(CacheConstant.DEVICE_CACHE_KEY.concat(deviceInfo.getDeviceId()), deviceInfo);
        return ok.equals(CacheConstant.SUCCESS);
    }

    @Override
    public <T> boolean updateDeviceField(String deviceId, String fieldName, T value) {
        String key = CacheConstant.DEVICE_CACHE_KEY.concat(deviceId);
        if (!redisJsonService.exist(key)) {
            return false;
        }
        String ok = redisJsonService.updateField(key, fieldName, value);
        return ok.equals(CacheConstant.SUCCESS);
    }

    @Override
    public boolean updateDeviceInfo(String deviceId, DeviceInfoUpdateModel model) {
        String key = CacheConstant.DEVICE_CACHE_KEY.concat(deviceId);
        if (!redisJsonService.exist(key)) {
            return false;
        }
        Mirror<DeviceInfoUpdateModel> mir = Mirror.me(model);
        for (Field field : mir.getFields()) {
            if (null != mir.getValue(model, field.getName())) {
                String ok = redisJsonService.updateField(key, field.getName(), mir.getValue(model, field.getName()));
                if (!ok.equals(CacheConstant.SUCCESS)) {
                    throw new RuntimeException("update device info redis json cache fail");
                }
            }
        }
        return true;
    }

    @Override
    public DeviceProcessCache getDeviceCache(String deviceId) {
        DeviceProcessCache object = redisJsonService.getObject(CacheConstant.DEVICE_CACHE_KEY.concat(deviceId), DeviceProcessCache.class);
        return object;
    }

    @Override
    public boolean deleteDeviceCache(String deviceId) {
        long re = redisJsonService.delObject(CacheConstant.DEVICE_CACHE_KEY.concat(deviceId));
        return re != 0;
    }
}
