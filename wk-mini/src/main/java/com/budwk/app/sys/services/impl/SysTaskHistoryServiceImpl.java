package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_task_history;
import com.budwk.app.sys.services.SysTaskHistoryService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysTaskHistoryServiceImpl extends BaseServiceImpl<Sys_task_history> implements SysTaskHistoryService {
    public SysTaskHistoryServiceImpl(Dao dao) {
        super(dao);
    }
}
