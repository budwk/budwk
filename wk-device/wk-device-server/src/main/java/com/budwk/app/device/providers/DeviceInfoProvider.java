package com.budwk.app.device.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.device.enums.DeviceOnline;
import com.budwk.app.device.enums.DeviceState;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_info_state;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.services.DeviceInfoService;
import com.budwk.app.device.services.DeviceInfoStateService;
import com.budwk.app.device.services.DeviceProductService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@Service(interfaceClass = IDeviceInfoProvider.class)
@IocBean
public class DeviceInfoProvider implements IDeviceInfoProvider {
    @Inject
    private DeviceProductService deviceProductService;
    @Inject
    private DeviceInfoService deviceInfoService;
    @Inject
    private DeviceInfoStateService deviceInfoStateService;

    @Override
    public Device_product getProduct(String productId) {
        return deviceProductService.getProductWithCache(productId);
    }

    @Override
    public Device_info getDevice(Cnd cnd) {
        return deviceInfoService.fetch(cnd);
    }

    @Override
    public void updateDeviceOnline(String deviceId, DeviceOnline online) {
        Device_info_state deviceInfoState = deviceInfoStateService.fetch(deviceId);
        if (deviceInfoState == null) {
            deviceInfoState = new Device_info_state();
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setState(DeviceState.NORMAL);
            deviceInfoState.setOnline(online);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.insert(deviceInfoState);
        } else {
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setOnline(online);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.updateIgnoreNull(deviceInfoState);
        }
    }

    @Override
    public void updateDeviceState(String deviceId, DeviceState state) {
        Device_info_state deviceInfoState = deviceInfoStateService.fetch(deviceId);
        if (deviceInfoState == null) {
            deviceInfoState = new Device_info_state();
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setState(state);
            deviceInfoState.setOnline(DeviceOnline.ONLINE);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.insert(deviceInfoState);
        } else {
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setState(state);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.updateIgnoreNull(deviceInfoState);
        }
    }

    @Override
    public void updateDeviceState(String deviceId, DeviceState state, DeviceOnline online) {
        Device_info_state deviceInfoState = deviceInfoStateService.fetch(deviceId);
        if (deviceInfoState == null) {
            deviceInfoState = new Device_info_state();
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setState(state);
            deviceInfoState.setOnline(online);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.insert(deviceInfoState);
        } else {
            deviceInfoState.setDeviceId(deviceId);
            deviceInfoState.setState(state);
            deviceInfoState.setOnline(online);
            deviceInfoState.setReceiveTime(System.currentTimeMillis());
            deviceInfoStateService.updateIgnoreNull(deviceInfoState);
        }
    }
}
