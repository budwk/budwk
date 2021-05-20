package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_role_menu;
import com.budwk.app.sys.services.SysRoleMenuService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysRoleMenuServiceImpl extends BaseServiceImpl<Sys_role_menu> implements SysRoleMenuService {
    public SysRoleMenuServiceImpl(Dao dao) {
        super(dao);
    }
}
