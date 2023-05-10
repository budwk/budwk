package com.budwk.app.device.gateway;

import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.gateway.config.DeviceGatewayConfiguration;

/**
 * @author wizzer.cn
 */
public interface DeviceGatewayBuilder {
    /**
     * 获取ID
     *
     * @return
     */
    String getId();

    /**
     * 获取名称
     *
     * @return
     */
    String getName();

    /**
     * 获取传输协议
     *
     * @return
     */
    ProtocolType getProtocolType();

    /**
     * 构造网关
     *
     * @param configuration 配置参数
     * @return
     */
    DeviceGateway buildGateway(DeviceGatewayConfiguration configuration);
}
