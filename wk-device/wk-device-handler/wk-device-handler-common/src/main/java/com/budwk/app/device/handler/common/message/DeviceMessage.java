package com.budwk.app.device.handler.common.message;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
public interface DeviceMessage extends Serializable {

    /**
     * 消息组ID 用于区分不同类型数据的组
     */
    default String getGroupId() {
        return null;
    }

    /**
     * 消息ID
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