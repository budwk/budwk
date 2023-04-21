package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_menu_field;
import com.budwk.app.device.services.DeviceProductMenuFieldService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductMenuFieldServiceImpl extends BaseServiceImpl<Device_product_menu_field> implements DeviceProductMenuFieldService {
    public DeviceProductMenuFieldServiceImpl(Dao dao) {
        super(dao);
    }
}