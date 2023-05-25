package com.budwk.app.device.handler.common.codec.impl;

import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.message.DeviceMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据上报解析结果
 * @author wizzer.cn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodeReportResult implements DecodeResult {
    private static final long serialVersionUID = 6100256851048650501L;
    /**
     * 设备Id
     */
    private String deviceId;
    /**
     * 解码消息
     */
    private List<DeviceMessage> messages;

    /**
     * 协议code
     */
    private String handlerCode;

    public DecodeReportResult(String deviceId, List<DeviceMessage> messages) {
        this.deviceId = deviceId;
        this.messages = messages;
    }
}
