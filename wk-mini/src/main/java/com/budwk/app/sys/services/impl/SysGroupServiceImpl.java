package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_group;
import com.budwk.app.sys.models.Sys_role;
import com.budwk.app.sys.services.SysGroupService;
import com.budwk.app.sys.services.SysRoleService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysGroupServiceImpl extends BaseServiceImpl<Sys_group> implements SysGroupService {
    public SysGroupServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysRoleService sysRoleService;

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void clearGroup(String groupId) {
        List<Sys_role> roleList = sysRoleService.query(Cnd.where("groupId", "=", groupId));
        for (Sys_role role : roleList) {
            sysRoleService.clearRole(role.getId());
        }
        this.delete(groupId);
    }
}
