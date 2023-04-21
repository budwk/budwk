package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_menu;
import com.budwk.app.device.services.DeviceProductMenuService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductMenuServiceImpl extends BaseServiceImpl<Device_product_menu> implements DeviceProductMenuService {
    public DeviceProductMenuServiceImpl(Dao dao) {
        super(dao);
    }
}