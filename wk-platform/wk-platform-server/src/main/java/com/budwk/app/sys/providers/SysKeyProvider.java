package com.budwk.app.sys.providers;

import com.budwk.app.sys.services.SysKeyService;
import org.apache.dubbo.config.annotation.DubboService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@DubboService(interfaceClass = ISysKeyProvider.class)
@IocBean
public class SysKeyProvider implements ISysKeyProvider{
    @Inject
    private SysKeyService sysKeyService;

    @Override
    public String getAppkey(String appid) {
        return sysKeyService.getAppkey(appid);
    }
}
