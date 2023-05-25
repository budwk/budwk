package com.budwk.app.device.services.impl;

import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.services.DeviceCommandRecordService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceCommandRecordServiceImpl extends BaseServiceImpl<Device_cmd_record> implements DeviceCommandRecordService {
    public DeviceCommandRecordServiceImpl(Dao dao) {
        super(dao);
    }


    @Override
    public Device_cmd_record getNeedSendCommand(String deviceId) {
        return this.fetch(Cnd.where("deviceId", "=", deviceId).and("status", "=", CommandStatus.WAIT.value()).asc("id"));
    }
}