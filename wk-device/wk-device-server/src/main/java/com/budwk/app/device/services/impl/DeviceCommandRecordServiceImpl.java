package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_command_record;
import com.budwk.app.device.services.DeviceCommandRecordService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceCommandRecordServiceImpl extends BaseServiceImpl<Device_command_record> implements DeviceCommandRecordService {
    public DeviceCommandRecordServiceImpl(Dao dao) {
        super(dao);
    }
}