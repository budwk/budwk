package com.budwk.app.device.providers;

import com.budwk.app.device.objects.dto.DeviceHandlerDTO;

/**
 * @author wizzer.cn
 */
public interface IDeviceHandlerProvider {
    DeviceHandlerDTO getHandler(String code);
}
