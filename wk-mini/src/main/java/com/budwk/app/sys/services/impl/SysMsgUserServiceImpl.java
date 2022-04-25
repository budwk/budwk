package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_msg_user;
import com.budwk.app.sys.services.SysMsgUserService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysMsgUserServiceImpl extends BaseServiceImpl<Sys_msg_user> implements SysMsgUserService {
    public SysMsgUserServiceImpl(Dao dao) {
        super(dao);
    }
}
