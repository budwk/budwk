package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_user_pwd;
import com.budwk.app.sys.services.SysUserPwdService;
import com.budwk.starter.database.service.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@Slf4j
public class SysUserPwdServiceImpl extends BaseServiceImpl<Sys_user_pwd> implements SysUserPwdService {
    public SysUserPwdServiceImpl(Dao dao) {
        super(dao);
    }

}
