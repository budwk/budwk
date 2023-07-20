package com.budwk.app.device.handler.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.handler.common.codec.context.DeviceEventContext;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.providers.IDeviceInfoProvider;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import java.util.Map;

/**
 * @author wizzer.cn
 */
@IocBean
public class DefaultDeviceEventContext implements DeviceEventContext {
    @Inject
    @Reference
    private IDeviceInfoProvider deviceInfoProvider;

    @Override
    public void updateDeviceField(String deviceId, String key, Object value) {
        this.updateDeviceField(deviceId, Map.of(key, value));
    }

    @Override
    public void updateDeviceField(String deviceId, Map<String, Object> kv) {
        Device_info deviceInfo = Lang.map2Object(kv, Device_info.class);
        deviceInfo.setId(deviceId);
        deviceInfoProvider.updateIgnoreNull(deviceInfo);
    }

}
