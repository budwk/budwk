package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_type;
import com.budwk.app.device.services.DeviceTypeService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceTypeServiceImpl extends BaseServiceImpl<Device_type> implements DeviceTypeService {
    public DeviceTypeServiceImpl(Dao dao) {
        super(dao);
    }
}