package com.budwk.app.device.gateway.config;

import lombok.Data;
import org.nutz.lang.util.NutMap;

/**
 * 网关配置
 *
 * @author wizzer.cn
 */
@Data
public class DeviceGatewayConfiguration {
    /**
     * 网关标识
     */
    private String id;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 协议解析包标识
     */
    private String handlerCode;

    /**
     * 传输协议
     */
    private String protocolType;

    /**
     * 配置项
     */
    private NutMap properties;
}
