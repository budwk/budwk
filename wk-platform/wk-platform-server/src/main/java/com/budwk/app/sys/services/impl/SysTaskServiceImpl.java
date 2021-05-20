package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_task;
import com.budwk.app.sys.services.SysTaskService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysTaskServiceImpl extends BaseServiceImpl<Sys_task> implements SysTaskService {
    public SysTaskServiceImpl(Dao dao) {
        super(dao);
    }
}
