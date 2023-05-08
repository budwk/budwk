package com.budwk.app.device.handler.common.codec;

import com.budwk.app.device.handler.common.device.ProductInfo;

/**
 * 设备操作
 * @author wizzer.cn
 */
public interface DeviceOperator {
    /**
     * 获取设备id
     *
     * @return
     */
    String getDeviceId();

    /**
     * 会话id
     *
     * @return
     */
    String getSessionId();

    /**
     * 网关服务
     *
     * @return
     */
    String getGateway();

    /**
     * 是否在线
     *
     * @return
     */
    boolean isOnline();

    /**
     * 上线时间
     *
     * @return
     */
    long getOnlineTime();

    /**
     * 离线时间
     *
     * @return
     */
    long getOfflineTime();


    /**
     * 获取设备的一些属性和参数
     *
     * @param key
     * @return
     */
    Object getProperty(String key);


    /**
     * 获取设备的一些属性和参数，并转换为指定类型。注意：请确认数据类型正确否则会抛出类型转换异常
     *
     * @param type
     * @param key
     * @return
     */
    <T> T getProperty(Class<T> type, String key);

    void setProperty(String key, Object value);

    /**
     * 获取待下发指令数量
     *
     * @return
     */
    int getWaitCmdCount();

    ProductInfo getProduct();
}
