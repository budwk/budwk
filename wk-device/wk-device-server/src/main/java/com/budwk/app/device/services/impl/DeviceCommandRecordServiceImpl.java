package com.budwk.app.device.services.impl;

import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.services.DeviceCommandRecordService;
import com.budwk.starter.database.ig.IdGenerator;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceCommandRecordServiceImpl extends BaseServiceImpl<Device_cmd_record> implements DeviceCommandRecordService {
    public DeviceCommandRecordServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private IdGenerator idGenerator;

    @Override
    public Device_cmd_record getNeedSendCommand(String deviceId) {
        return this.fetch(Cnd.where("deviceId", "=", deviceId).and("status", "=", CommandStatus.WAIT.value()).asc("id"));
    }

    @Override
    public Device_cmd_record getById(String commandId) {
        return this.fetch(commandId);
    }

    @Override
    public void updateField(Device_cmd_record cmdRecord) {
        this.updateIgnoreNull(cmdRecord);
    }

    @Override
    public void save(Device_cmd_record command) {
        String MONTH_FORMAT = "yyyyMM";
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern(MONTH_FORMAT));
        command.setId(prefix + "-" + idGenerator.next());
        this.insert(command);
    }

}