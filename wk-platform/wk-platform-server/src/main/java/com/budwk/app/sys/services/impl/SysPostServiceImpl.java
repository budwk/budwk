package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_post;
import com.budwk.app.sys.services.SysPostService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysPostServiceImpl extends BaseServiceImpl<Sys_post> implements SysPostService {
    public SysPostServiceImpl(Dao dao) {
        super(dao);
    }
}
