package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product_cmd_attr;
import com.budwk.app.device.services.DeviceProductCmdAttrService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceProductCmdAttrServiceImpl extends BaseServiceImpl<Device_product_cmd_attr> implements DeviceProductCmdAttrService {
    public DeviceProductCmdAttrServiceImpl(Dao dao) {
        super(dao);
    }
}