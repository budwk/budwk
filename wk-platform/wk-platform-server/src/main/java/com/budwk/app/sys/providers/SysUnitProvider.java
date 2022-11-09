package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.services.SysUnitService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = ISysUnitProvider.class)
@IocBean
public class SysUnitProvider implements ISysUnitProvider {
    @Inject
    private SysUnitService sysUnitService;

    @Override
    public String getMasterCompanyId(String unitId) {
        return sysUnitService.getMasterCompanyId(unitId);
    }

    @Override
    public String getMasterCompanyPath(String unitId) {
        return sysUnitService.getMasterCompanyPath(unitId);
    }

    @Override
    public Sys_unit getMasterCompany(String unitId) {
        return sysUnitService.getMasterCompany(unitId);
    }
}
