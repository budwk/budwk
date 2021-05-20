package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.services.SysConfigService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = ISysConfigProvider.class)
@IocBean
public class SysConfigProvider implements ISysConfigProvider {
    @Inject
    private SysConfigService sysConfigService;

    @Override
    public NutMap getMapAll(String appId) {
        return sysConfigService.getMapAll(appId);
    }

    @Override
    public NutMap getMapOpened(String appId) {
        return sysConfigService.getMapOpened(appId);
    }

    @Override
    public String getString(String appId, String key) {
        return sysConfigService.getString(appId,key);
    }

    @Override
    public boolean getBoolean(String appId, String key) {
        return sysConfigService.getBoolean(appId,key);
    }
}
