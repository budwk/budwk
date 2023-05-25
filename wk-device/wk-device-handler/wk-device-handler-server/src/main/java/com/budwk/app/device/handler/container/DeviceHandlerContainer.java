package com.budwk.app.device.handler.container;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.providers.IDeviceHandlerProvider;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean
public class DeviceHandlerContainer {
    @Inject
    @Reference(check = false)
    private IDeviceHandlerProvider deviceHandlerProvider;

}
