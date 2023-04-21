package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.services.DeviceProductService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductServiceImpl extends BaseServiceImpl<Device_product> implements DeviceProductService {
    public DeviceProductServiceImpl(Dao dao) {
        super(dao);
    }
}