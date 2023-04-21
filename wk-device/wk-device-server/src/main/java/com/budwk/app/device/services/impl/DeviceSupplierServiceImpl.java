package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_supplier;
import com.budwk.app.device.services.DeviceSupplierService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceSupplierServiceImpl extends BaseServiceImpl<Device_supplier> implements DeviceSupplierService {
    public DeviceSupplierServiceImpl(Dao dao) {
        super(dao);
    }
}