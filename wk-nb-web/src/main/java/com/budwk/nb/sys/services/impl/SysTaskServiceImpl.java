package com.budwk.nb.sys.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_task;
import com.budwk.nb.sys.services.SysTaskService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2016/12/22.
 */
@IocBean(args = {"refer:dao"})
public class SysTaskServiceImpl extends BaseServiceImpl<Sys_task> implements SysTaskService {
    public SysTaskServiceImpl(Dao dao) {
        super(dao);
    }
}
