package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_handler;
import com.budwk.app.device.services.DeviceHandlerService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceHandlerServiceImpl extends BaseServiceImpl<Device_handler> implements DeviceHandlerService {
    public DeviceHandlerServiceImpl(Dao dao) {
        super(dao);
    }
}