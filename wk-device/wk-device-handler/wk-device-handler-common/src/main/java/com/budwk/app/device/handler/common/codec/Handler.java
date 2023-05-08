package com.budwk.app.device.handler.common.codec;

import com.budwk.app.device.handler.common.codec.context.DeviceContext;
import com.budwk.app.device.handler.common.enums.TransportType;

/**
 * 解析器
 * @author wizzer.cn
 * @author zyang
 */
public interface Handler {
    /**
     * 解析器标识
     */
    String getCode();

    /**
     * 解析器名称
     */
    String getName();

    /**
     * 获取指定传输协议的编解码实现
     *
     * @param transportType 传输协议类型 {@link  TransportType}
     * @return 编解码实现
     * @see MessageCodec
     */
    MessageCodec getMessageCodec(TransportType transportType);

    /**
     * 设备注册事件。当系统添加设备时会触发这个事件
     *
     * @param context        事件上下文
     * @param deviceOperator 设备操作类
     */
    default void onDeviceRegistered(DeviceContext context, DeviceOperator deviceOperator) {
    }

    /**
     * 设备注销事件。当系统删除设备时会触发这个事件
     *
     * @param context        事件上下文
     * @param deviceOperator 设备操作类
     */
    default void onDeviceUnRegistered(DeviceContext context, DeviceOperator deviceOperator) {
    }
}
