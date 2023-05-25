package com.budwk.app.device.providers;

import com.budwk.app.device.models.Device_cmd_record;

/**
 * @author wizzer.cn
 */
public interface IDeviceCommandProvider {
    /**
     * 获取待下发指令数量
     * @param deviceId 设备ID
     * @return
     */
    int getWaitCount(String deviceId);

    /**
     * 获取最早待下发指令
     * @param deviceId 设备ID
     * @return
     */
    Device_cmd_record getNeedSendCommand(String deviceId);
}
