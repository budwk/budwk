package com.budwk.app.device.services;

import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.objects.dto.CommandInfoDTO;

import java.util.Map;

/**
 * @author wizzer.cn
 */
public interface DeviceCommandService {
    void createCommand(String deviceId, String method, String commandName, Map<String, Object> params, String operator, String reason);

    void createCommand(Device_info deviceInfo, String method, String commandName, Map<String, Object> params, String operator, String reason);

    Device_cmd_record createCommand(CommandInfoDTO commandInfo);
}
