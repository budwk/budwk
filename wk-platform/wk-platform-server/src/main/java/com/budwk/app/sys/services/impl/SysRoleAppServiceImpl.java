package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_role_app;
import com.budwk.app.sys.services.SysRoleAppService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysRoleAppServiceImpl extends BaseServiceImpl<Sys_role_app> implements SysRoleAppService {
    public SysRoleAppServiceImpl(Dao dao) {
        super(dao);
    }
}
