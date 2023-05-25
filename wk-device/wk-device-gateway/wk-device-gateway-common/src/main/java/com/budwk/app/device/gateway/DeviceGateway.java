package com.budwk.app.device.gateway;

import com.budwk.app.device.enums.ProtocolType;

/**
 * 网关接口
 * @author wizzer.cn
 */
public interface DeviceGateway {
    /**
     * 获取传输协议
     * @return
     */
    ProtocolType getProtocolType();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

    /**
     * @return 是否存活
     */
    boolean isAlive();
}
