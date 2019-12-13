package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.sys.models.Sys_app_task;
import com.budwk.nb.sys.services.SysAppTaskService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by wizzer on 2019/2/27.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysAppTaskService.class)
public class SysAppTaskServiceImpl extends BaseServiceImpl<Sys_app_task> implements SysAppTaskService {
    public SysAppTaskServiceImpl(Dao dao) {
        super(dao);
    }

}