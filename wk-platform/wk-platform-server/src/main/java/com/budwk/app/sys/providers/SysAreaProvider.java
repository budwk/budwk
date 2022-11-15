package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.models.Sys_area;
import com.budwk.app.sys.services.SysAreaService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;

@Service(interfaceClass = ISysAreaProvider.class)
@IocBean
public class SysAreaProvider implements ISysAreaProvider {
    @Inject
    private SysAreaService sysAreaService;

    /**
     * 通过code获取子级(code为空返回第一级)
     *
     * @param code 标识
     * @return
     */
    public List<Sys_area> getSubListByCode(String code) {
        return sysAreaService.getSubListByCode(code);
    }


    /**
     * 通过code获取子级(code为空返回第一级)
     *
     * @param code 标识
     * @return
     */
    public NutMap getSubMapByCode(String code) {
        return sysAreaService.getSubMapByCode(code);
    }
}
