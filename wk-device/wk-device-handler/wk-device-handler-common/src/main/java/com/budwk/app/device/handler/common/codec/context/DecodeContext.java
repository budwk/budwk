package com.budwk.app.device.handler.common.codec.context;

import com.budwk.app.device.handler.common.codec.CacheStore;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.DeviceRegistry;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;

/**
 * 解码上下文
 * @author wizzer.cn
 */
public interface DecodeContext {
    /**
     * 获取资源装载对象
     * @return
     */
    default DeviceRegistry getDeviceRegistry() {
        return null;
    }
    /**
     * 获取上报的消息
     *
     * @return 设备上报的消息
     */
    EncodedMessage getMessage();

    /**
     * 根据设备通信号获取设备
     *
     * @param deviceNo 设备通信号
     * @return 设备信息
     */
    DeviceOperator getDevice(String deviceNo);

    /**
     * 根据指定字段获取设备信息
     *
     * @param field 字段
     * @param value 值
     * @return 设备信息
     */
    DeviceOperator getDevice(String field, String value);

    /**
     * 获取网关设备（集中器）
     *
     * @param field 字段
     * @param value 值
     * @return 设备信息
     */
    DeviceOperator getGatewayDevice(String field, String value);

    /**
     * 获取当前上下文的设备信息
     *
     * @return 设备信息
     */
    DeviceOperator getDevice();

    /**
     * 直接发送消息给设备。注意：当前只有TCP/UDP协议才支持
     * 一般用于解析上报数据后对设备的回复
     *
     * @param replyMessage 需要发送的消息
     */
    void send(EncodedMessage replyMessage);

    /**
     * 获取缓存存储
     *
     * @param id 缓存存储的id
     * @return 如果已存在则返回，不存在则创建
     */
    CacheStore getCacheStore(String id);

    void setDevice(DeviceOperator deviceOperator);
}
