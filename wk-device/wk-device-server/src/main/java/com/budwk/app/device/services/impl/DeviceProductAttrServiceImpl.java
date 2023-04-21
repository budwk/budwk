package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_attr;
import com.budwk.app.device.services.DeviceProductAttrService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductAttrServiceImpl extends BaseServiceImpl<Device_product_attr> implements DeviceProductAttrService {
    public DeviceProductAttrServiceImpl(Dao dao) {
        super(dao);
    }
}