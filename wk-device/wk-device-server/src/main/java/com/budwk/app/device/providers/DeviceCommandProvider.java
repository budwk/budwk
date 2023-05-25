package com.budwk.app.device.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.services.DeviceCommandRecordService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */

@Service(interfaceClass = IDeviceCommandProvider.class)
@IocBean
public class DeviceCommandProvider implements IDeviceCommandProvider {
    @Inject
    private DeviceCommandRecordService deviceCommandRecordService;

    @Override
    public int getWaitCount(String deviceId) {
        return deviceCommandRecordService.count(Cnd.where("deviceId", "=", deviceId)
                .and("status", "=", CommandStatus.WAIT.value()));
    }

    @Override
    public Device_cmd_record getNeedSendCommand(String deviceId) {
        return deviceCommandRecordService.getNeedSendCommand(deviceId);
    }
}
