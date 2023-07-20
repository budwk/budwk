package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_info_meter;
import com.budwk.app.device.services.DeviceInfoMeterService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceInfoMeterServiceImpl extends BaseServiceImpl<Device_info_meter> implements DeviceInfoMeterService {
    public DeviceInfoMeterServiceImpl(Dao dao) {
        super(dao);
    }
}