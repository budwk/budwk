package com.budwk.app.device.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.objects.dto.CommandInfoDTO;
import com.budwk.app.device.services.DeviceCommandRecordHistoryService;
import com.budwk.app.device.services.DeviceCommandRecordService;
import com.budwk.app.device.services.DeviceCommandService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

/**
 * @author wizzer.cn
 */

@Service(interfaceClass = IDeviceCommandProvider.class)
@IocBean
public class DeviceCommandProvider implements IDeviceCommandProvider {
    @Inject
    private DeviceCommandRecordService deviceCommandRecordService;
    @Inject
    private DeviceCommandRecordHistoryService deviceCommandRecordHistoryService;
    @Inject
    private DeviceCommandService deviceCommandService;

    @Override
    public int getWaitCount(String deviceId) {
        return deviceCommandRecordService.count(Cnd.where("deviceId", "=", deviceId)
                .and("status", "=", CommandStatus.WAIT.value()));
    }

    @Override
    public Device_cmd_record getNeedSendCommand(String deviceId) {
        return deviceCommandRecordService.getNeedSendCommand(deviceId);
    }

    @Override
    public void createCommand(CommandInfoDTO cmdInfo) {
        deviceCommandService.createCommand(cmdInfo);
    }

    @Override
    public Device_cmd_record getById(String commandId) {
        if (Strings.isBlank(commandId)) {
            return null;
        }
        return deviceCommandRecordService.getById(commandId);
    }

    @Override
    public void markSend(String commandId, long sendTime) {
        Device_cmd_record commandRecord = new Device_cmd_record();
        commandRecord.setId(commandId);
//        commandRecord.setStatus(DeviceCommandStatusEnum.SEND);
        commandRecord.setSendAt(sendTime);
        deviceCommandRecordService.updateField(commandRecord);
    }

    @Override
    public void markFinished(String commandId, CommandStatus status, long finishTime, String respAttribute) {
        Device_cmd_record commandRecord = deviceCommandRecordService.getById(commandId);
        commandRecord.setId(commandId);
        commandRecord.setStatus(status);
        commandRecord.setFinishAt(finishTime);
        commandRecord.setRespAttribute(respAttribute);
        deviceCommandRecordHistoryService.save(commandRecord);
        deviceCommandRecordService.delete(commandId);
    }
}
