package com.budwk.app.device.storage;

import com.budwk.app.device.storage.storages.*;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 设备原始报文和上报数据 存储方式的选择切换
 * @author wizzer.cn
 */
@IocBean
public class DeviceStorageStarter {
    @Inject
    private PropertiesProxy conf;
    @Inject
    private Ioc ioc;

    private static final String CONF_PREFIX = "device.storage.";

    @IocBean
    public DeviceRawDataStorage getDeviceRawDataStorage() {
        return ioc.get(MongoDeviceRawDataStorageImpl.class);
    }


    @IocBean
    public DeviceDataStorage getDeviceDataStorage() {
        String mode = conf.get(CONF_PREFIX + "data-save-type", "mongodb");
        switch (mode) {
            case "mongodb":
                return ioc.get(MongoDeviceDataStorageImpl.class);
            case "tdengine":
                return ioc.get(TDEngineDeviceDataStorageImpl.class);
            default:
                throw new RuntimeException("没有对应的实现");
        }
    }
}
