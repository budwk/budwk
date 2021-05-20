package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_unit_user;
import com.budwk.app.sys.services.SysUnitUserService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysUnitUserServiceImpl extends BaseServiceImpl<Sys_unit_user> implements SysUnitUserService {
    public SysUnitUserServiceImpl(Dao dao) {
        super(dao);
    }
}
