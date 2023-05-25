package com.budwk.app.device.gateway.manager;

import com.budwk.app.device.gateway.DeviceGateway;
import com.budwk.app.device.gateway.DeviceGatewayBuilder;
import com.budwk.app.device.gateway.DeviceGatewayManager;
import com.budwk.app.device.gateway.config.DeviceGatewayConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer.cn
 */

@Slf4j
@IocBean(name = "defaultGatewayManager")
public class DefaultGatewayManager implements DeviceGatewayManager {
    @Inject
    private PropertiesProxy conf;

    private List<DeviceGateway> startedGatewayList = new ArrayList<>();

    @Inject
    private Ioc ioc;

    public void start(DeviceGateway deviceGateway) {
        deviceGateway.start();
    }

    @Override
    public List<DeviceGateway> loadGateway() {
        if (startedGatewayList.size() == 0) {
            NutMap gatewayConfig = loadGatewayConfig();
            for (String gatewayId : gatewayConfig.keySet()) {
                NutMap config = gatewayConfig.getAs(gatewayId, NutMap.class);
                DeviceGatewayConfiguration configuration = Lang.map2Object(config, DeviceGatewayConfiguration.class);
                configuration.setId(gatewayId);
                DeviceGateway gateway = createGateway(configuration);
                if (null != gateway) {
                    startedGatewayList.add(gateway);
                }
            }
        }
        return startedGatewayList;
    }

    private DeviceGateway createGateway(DeviceGatewayConfiguration configuration) {
        if (ioc.has(configuration.getProtocolType() + "GatewayBuilder")) {
            DeviceGatewayBuilder gatewayBuilder = ioc.get(DeviceGatewayBuilder.class, configuration.getProtocolType() + "GatewayBuilder");
            if (null == gatewayBuilder) {
                log.warn("[gateway] 未找到标识为{}的网关构造器失败", configuration.getProtocolType());
                return null;
            }
            return gatewayBuilder.buildGateway(configuration);
        }
        return null;
    }

    private NutMap loadGatewayConfig() {

        List<String> gateway = conf.getKeysWithPrefix("gateway");
        NutMap result = NutMap.NEW();
        gateway.forEach(it -> {
            String[] paths = it.split("\\.");
            NutMap tmp = result;
            int i = 0;
            int len = paths.length;
            for (String path : paths) {
                i++;
                if (i == len) {
                    tmp.put(path, conf.get(it));
                } else {
                    NutMap o = tmp.getAs(path, NutMap.class);
                    if (null == o) {
                        o = NutMap.NEW();
                        tmp.put(path, o);
                    }
                    tmp = o;
                }
            }
        });
        return result.getAs("gateway", NutMap.class);
    }
}
