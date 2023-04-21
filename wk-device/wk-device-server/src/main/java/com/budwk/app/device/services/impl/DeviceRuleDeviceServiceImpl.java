package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_rule_device;
import com.budwk.app.device.services.DeviceRuleDeviceService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceRuleDeviceServiceImpl extends BaseServiceImpl<Device_rule_device> implements DeviceRuleDeviceService {
    public DeviceRuleDeviceServiceImpl(Dao dao) {
        super(dao);
    }
}