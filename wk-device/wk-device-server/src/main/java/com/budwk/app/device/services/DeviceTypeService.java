package com.budwk.app.device.services;

import com.budwk.app.device.models.Device_type;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer.cn
 */
public interface DeviceTypeService extends BaseService<Device_type> {

    void save(Device_type deviceType, String pid);

    void deleteAndChild(Device_type deviceType);
}