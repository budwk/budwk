package com.budwk.app.device.gateway.server;

import com.budwk.app.device.gateway.DeviceGateway;
import com.budwk.app.device.gateway.DeviceGatewayManager;
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

    @Override
    public void stop() throws Exception {
        List<DeviceGateway> gatewayList = deviceGatewayManager.loadGateway();
        gatewayList.forEach(this::stopGateway);
    }

    private void startGateway(DeviceGateway deviceGateway) {
        deviceGateway.start();
    }

    private void stopGateway(DeviceGateway deviceGateway) {
        deviceGateway.stop();
    }
}
