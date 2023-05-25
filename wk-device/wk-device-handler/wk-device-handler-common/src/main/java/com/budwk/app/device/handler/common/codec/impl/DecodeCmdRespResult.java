package com.budwk.app.device.handler.common.codec.impl;

import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.message.DeviceMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 指令回复结果
 *
 * @author wizzer.cn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodeCmdRespResult implements DecodeResult {
    private static final long serialVersionUID = 8454610470690254614L;
    /**
     * 指令ID
     */
    private String commandId;
    /**
     * 设备id。注意，可能为空
     */
    private String deviceId;
    /**
     * 执行的指令方法
     */
    private String commandCode;
    /**
     * 指令是否成功执行
     */
    private boolean success;

    /**
     * 响应结果消息
     */
    private List<DeviceMessage> messages;

    private String handlerCode;

    public DecodeCmdRespResult(String commandId, DeviceResponseMessage responseMessage) {
        this.commandId = commandId;
        this.success = responseMessage.isSuccess();
        this.commandCode = responseMessage.getCommandCode();
        this.deviceId = responseMessage.getDeviceId();
        this.messages = Arrays.asList(responseMessage);
    }

    public DecodeCmdRespResult(String commandId, String deviceId, String commandCode, boolean success, List<DeviceMessage> messages) {
        this.commandId = commandId;
        this.deviceId = deviceId;
        this.commandCode = commandCode;
        this.success = success;
        this.messages = messages;
    }
}