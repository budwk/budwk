package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_info_state;
import com.budwk.app.device.services.DeviceInfoStateService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceInfoStateServiceImpl extends BaseServiceImpl<Device_info_state> implements DeviceInfoStateService {
    public DeviceInfoStateServiceImpl(Dao dao) {
        super(dao);
    }
}