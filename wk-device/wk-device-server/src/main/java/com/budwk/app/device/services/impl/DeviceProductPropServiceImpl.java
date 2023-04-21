package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_prop;
import com.budwk.app.device.services.DeviceProductPropService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductPropServiceImpl extends BaseServiceImpl<Device_product_prop> implements DeviceProductPropService {
    public DeviceProductPropServiceImpl(Dao dao) {
        super(dao);
    }
}