package com.budwk.app.device.gateway.tcp;

import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.gateway.DeviceGateway;
import com.budwk.app.device.gateway.DeviceGatewayBuilder;
import com.budwk.app.device.gateway.config.DeviceGatewayConfiguration;
import com.budwk.app.device.message.MessageTransfer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(name = "tcpGatewayBuilder")
public class TcpDeviceGatewayBuilder implements DeviceGatewayBuilder {
    @Inject
    private MessageTransfer messageTransfer;

    @Override
    public String getId() {
        return "tcp";
    }

    @Override
    public String getName() {
        return "TCP";
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.TCP;
    }

    @Override
    public DeviceGateway buildGateway(DeviceGatewayConfiguration configuration) {
        return new TcpDeviceGateway(configuration, messageTransfer);
    }
}