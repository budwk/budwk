package com.budwk.app.device.gateway;

import java.util.List;

/**
 * 设备网关管理
 * @author wizzer.cn
 */
public interface DeviceGatewayManager {
    List<DeviceGateway> loadGateway();
}
