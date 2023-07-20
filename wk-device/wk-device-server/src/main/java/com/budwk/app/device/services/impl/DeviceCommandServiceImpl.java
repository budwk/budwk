package com.budwk.app.device.services.impl;

import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.message.MqMessage;
import com.budwk.app.device.message.MqTopic;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.models.Device_product_cmd;
import com.budwk.app.device.objects.dto.CommandInfoDTO;
import com.budwk.app.device.services.*;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.database.ig.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class DeviceCommandServiceImpl implements DeviceCommandService {

    @Inject
    private IdGenerator idGenerator;
    @Inject
    private RedisService redisService;

    @Inject
    private DeviceInfoService deviceInfoService;

    @Inject
    private DeviceProductCmdService deviceProductCmdService;

    @Inject
    private DeviceCommandRecordService deviceCommandRecordService;

    @Inject
    private DeviceCommandRecordHistoryService deviceCommandRecordHistoryService;

    @Inject
    private MessageTransfer messageTransfer;

    @Override
    public void createCommand(String deviceId, String method, String commandName, Map<String, Object> params, String operator, String reason) {
        Device_info deviceInfo = deviceInfoService.fetch(deviceId);
        if (null == deviceInfo) {
            throw new BaseException("设备不存在");
        }
        this.createCommand(deviceInfo, method, commandName, params, operator, reason);
    }

    @Override
    public void createCommand(Device_info deviceInfo, String commandCode, String commandName, Map<String, Object> params, String operator, String reason) {
        Device_cmd_record command = new Device_cmd_record();
        command.setId(getId());
        command.setProductId(deviceInfo.getProductId());
        command.setCommandId(Lang.md5(commandCode + deviceInfo.getProductId()));
        command.setCommandName(commandName);
        command.setCreatedAt(System.currentTimeMillis());
        command.setDeviceId(deviceInfo.getId());
        command.setDeviceNo(deviceInfo.getDeviceNo());
        //command.setAccountNo(deviceInfo.getAccountNo());
        command.setCommandCode(commandCode);
        command.setParams(params);
        command.setSerialNo(this.getSerialNo(deviceInfo.getId()));
        command.setStatus(CommandStatus.WAIT);
        command.setOperator(operator);
        command.setReason(reason);
        this.cancelHistoryCmd(command);
        deviceCommandRecordService.save(command);
        this.triggerCmd(command);
    }

    private void cancelHistoryCmd(Device_cmd_record command) {
        List<Device_cmd_record> list =
                deviceCommandRecordService.query(Cnd.where("commandId", "=", command.getCommandId())
                        .and("deviceId", "=", command.getDeviceId()));
        List<String> ids = new ArrayList<>(list.size());
        list.forEach(it -> {
            ids.add(it.getId());
            it.setStatus(CommandStatus.CANCELED);
            it.setUpdatedAt(System.currentTimeMillis());
        });
        deviceCommandRecordHistoryService.save(list);
        deviceCommandRecordService.delete(ids);
    }

    @Override
    public Device_cmd_record createCommand(CommandInfoDTO commandInfo) {

        Device_cmd_record commandRecord = new Device_cmd_record();
        if (Strings.isBlank(commandInfo.getCommandId())) {
            commandInfo.setCommandId(Lang.md5(commandInfo.getCommandCode() + commandInfo.getProductId()));
        }
        Device_product_cmd command = deviceProductCmdService.fetch(commandInfo.getCommandId());
        if (null == command) {
            throw new BaseException("指令不存在");
        }
        commandInfo.setCommandCode(command.getCode());
        commandInfo.setCommandName(command.getName());
        commandRecord.setId(getId());
        commandRecord.setProductId(Strings.sBlank(commandInfo.getProductId(), command.getProductId()));
        commandRecord.setCommandId(commandInfo.getCommandId());
        commandRecord.setCommandName(commandInfo.getCommandName());
        commandRecord.setCreatedAt(System.currentTimeMillis());
        commandRecord.setDeviceId(commandInfo.getDeviceId());
        commandRecord.setDeviceNo(commandInfo.getDeviceNo());
        //commandRecord.setAccountNo(commandInfo.getAccountNo());
        commandRecord.setCommandCode(commandInfo.getCommandCode());
        commandRecord.setParams(commandInfo.getParams());
        commandRecord.setSerialNo(this.getSerialNo(commandInfo.getDeviceId()));
        commandRecord.setStatus(CommandStatus.WAIT);
        commandRecord.setOperator(Strings.sBlank(commandInfo.getOperator(), "system"));
        commandRecord.setReason(commandInfo.getNote());
        this.cancelHistoryCmd(commandRecord);
        deviceCommandRecordService.save(commandRecord);
        this.triggerCmd(commandRecord);
        return commandRecord;
    }

    private void triggerCmd(Device_cmd_record command) {
        CommandInfoDTO dto = new CommandInfoDTO();
        dto.setId(command.getId());
        dto.setDeviceId(command.getDeviceId());
        dto.setCommandCode(command.getCommandCode());
        dto.setParams(command.getParams());
        MqMessage<CommandInfoDTO> message = new MqMessage<>(MqTopic.DEVICE_CMD_TRIGGER, dto);
        messageTransfer.publish(message);
    }

    private Integer getSerialNo(String deviceId) {
//        Long incr = redisService.incr(RedisConstant.REDIS_CMD_SERIAL_NO_KYE_PREFIX + deviceId);
//        return (int) (incr % 65536);
        return Math.toIntExact(System.currentTimeMillis() % 65536);
    }

    private String getId() {
        YearMonth now = YearMonth.now();
        return String.format("%d%02d-%s", now.getYear(), now.getMonthValue(), idGenerator.next());
    }
}
