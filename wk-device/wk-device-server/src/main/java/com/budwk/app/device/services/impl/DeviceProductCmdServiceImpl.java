package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_cmd;
import com.budwk.app.device.services.DeviceProductCmdService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductCmdServiceImpl extends BaseServiceImpl<Device_product_cmd> implements DeviceProductCmdService {
    public DeviceProductCmdServiceImpl(Dao dao) {
        super(dao);
    }
}