package com.budwk.app.device.handler.common.message;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
public interface DeviceMessage extends Serializable {

    /**
     * 消息类型
     */
    default String getMessageType() {
        return null;
    }

    /**
     * 消息ID,用于请求响应模式下关联,可能大部分设备通信用不到此
     */
    String getMessageId();

    /**
     * 设备ID
     */
    String getDeviceId();

    /**
     * 时间戳，注意：这个时间戳就是最终存入时序数据库中的时间
     */
    long getTimestamp();
}