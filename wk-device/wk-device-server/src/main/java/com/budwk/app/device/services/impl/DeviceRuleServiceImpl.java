package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_rule;
import com.budwk.app.device.services.DeviceRuleService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceRuleServiceImpl extends BaseServiceImpl<Device_rule> implements DeviceRuleService {
    public DeviceRuleServiceImpl(Dao dao) {
        super(dao);
    }
}