package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.services.DeviceInfoService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceInfoServiceImpl extends BaseServiceImpl<Device_info> implements DeviceInfoService {
    public DeviceInfoServiceImpl(Dao dao) {
        super(dao);
    }
}