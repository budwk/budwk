package com.budwk.app.device.handler.common.codec.result;

import com.budwk.app.device.handler.common.message.DeviceMessage;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer.cn
 */
public interface DecodeResult extends Serializable {

    /**
     * 获取设备id
     *
     * @return
     */
    String getDeviceId();

    /**
     * 获取解码后消息
     *
     * @return
     */
    List<DeviceMessage> getMessages();

    /**
     * 解析器标识
     * @return
     */
    String getHandlerCode();
}
