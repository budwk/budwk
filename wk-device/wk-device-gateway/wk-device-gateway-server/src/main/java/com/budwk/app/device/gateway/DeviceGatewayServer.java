package com.budwk.app.device.gateway;

import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean
public class DeviceGatewayServer implements ServerFace {
    @Inject
    private DeviceGatewayManager deviceGatewayManager;

    @Override
    public void start() throws Exception {
        List<DeviceGateway> gatewayList = deviceGatewayManager.loadGateway();
        gatewayList.forEach(this::startGateway);
    }

    private void startGateway(DeviceGateway deviceGateway) {
        deviceGateway.start();
    }
}
