package com.budwk.app.device.handler.common.codec.result;

import com.budwk.app.device.handler.common.message.DeviceMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 指令回复结果
 *
 * @author wizzer.cn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponseResult implements DecodeResult {
    private static final long serialVersionUID = -3532992412392691183L;
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
    /**
     * 解析器标识
     */
    private String handlerCode;

    public DefaultResponseResult(String commandId, String commandCode, DeviceResponseMessage responseMessage) {
        this.commandId = commandId;
        this.commandCode = commandCode;
        this.success = responseMessage.isSuccess();
        this.commandCode = responseMessage.getCommandCode();
        this.deviceId = responseMessage.getDeviceId();
        this.messages = List.of(responseMessage);
    }

    public DefaultResponseResult(String commandId, String deviceId, String commandCode, boolean success, List<DeviceMessage> messages) {
        this.commandId = commandId;
        this.deviceId = deviceId;
        this.commandCode = commandCode;
        this.success = success;
        this.messages = messages;
    }
}
