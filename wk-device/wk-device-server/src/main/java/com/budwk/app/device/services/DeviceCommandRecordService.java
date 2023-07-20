package com.budwk.app.device.services;

import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer.cn
 */
public interface DeviceCommandRecordService extends BaseService<Device_cmd_record> {
    /**
     * 或许最早待下发指令
     * @param deviceId 设备ID
     * @return
     */
    Device_cmd_record getNeedSendCommand(String deviceId);

    Device_cmd_record getById(String commandId);

    void updateField(Device_cmd_record cmdRecord);

    void save(Device_cmd_record command);
}