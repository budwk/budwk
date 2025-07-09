package com.budwk.app.sys.providers;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.services.SysAppService;
import org.apache.dubbo.config.annotation.DubboService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@DubboService(interfaceClass = ISysAppProvider.class)
@IocBean
public class SysAppProvider implements ISysAppProvider {
    @Inject
    private SysAppService sysAppService;

    @Override
    public List<Sys_app> listAll(){
        return sysAppService.listAll();
    }

    @Override
    public List<Sys_app> listEnable(){
        return sysAppService.listEnable();
    }
}
