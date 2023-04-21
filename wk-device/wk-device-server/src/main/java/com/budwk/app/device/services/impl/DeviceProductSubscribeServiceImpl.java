package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_subscribe;
import com.budwk.app.device.services.DeviceProductSubscribeService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductSubscribeServiceImpl extends BaseServiceImpl<Device_product_subscribe> implements DeviceProductSubscribeService {
    public DeviceProductSubscribeServiceImpl(Dao dao) {
        super(dao);
    }
}