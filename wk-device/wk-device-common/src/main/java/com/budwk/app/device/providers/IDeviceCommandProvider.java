package com.budwk.app.device.providers;

import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.objects.dto.CommandInfoDTO;

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

    Device_cmd_record getById(String commandId);
    /**
     * 创建设备指令
     *
     * @param cmdInfo 指令信息
     */
    void createCommand(CommandInfoDTO cmdInfo);

    void markSend(String commandId, long sendTime);

    void markFinished(String commandId, CommandStatus status, long finishTime, String respAttribute);

}
