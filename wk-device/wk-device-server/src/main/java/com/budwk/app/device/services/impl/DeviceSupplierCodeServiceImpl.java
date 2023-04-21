package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_supplier_code;
import com.budwk.app.device.services.DeviceSupplierCodeService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceSupplierCodeServiceImpl extends BaseServiceImpl<Device_supplier_code> implements DeviceSupplierCodeService {
    public DeviceSupplierCodeServiceImpl(Dao dao) {
        super(dao);
    }
}