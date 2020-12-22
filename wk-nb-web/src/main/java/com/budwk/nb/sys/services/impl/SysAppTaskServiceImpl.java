package com.budwk.nb.sys.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_app_task;
import com.budwk.nb.sys.services.SysAppTaskService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/2/27.
 */
@IocBean(args = {"refer:dao"})
public class SysAppTaskServiceImpl extends BaseServiceImpl<Sys_app_task> implements SysAppTaskService {
    public SysAppTaskServiceImpl(Dao dao) {
        super(dao);
    }

}