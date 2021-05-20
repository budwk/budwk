package com.budwk.app.ucenter.services.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.providers.ISysUserProvider;
import com.budwk.app.ucenter.services.AuthService;
import com.budwk.app.ucenter.services.ValidateService;
import com.budwk.starter.common.exception.BaseException;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 认证服务
 *
 * @author wizzer@qq.com
 */
@IocBean
public class AuthServiceImpl implements AuthService {

    @Inject
    private ValidateService validateService;

    @Inject
    @Reference(check = false)
    private ISysUserProvider sysUserProvider;

    public Sys_user loginByPassword(String loginname, String password,
                                    String key, String code) throws BaseException {
        validateService.checkCode(key, code);
        return sysUserProvider.loginByPassword(loginname, password);
    }

    public Sys_user loginByMobile(String mobile, String code) throws BaseException {
        validateService.checkSMSCode(mobile, code);
        return sysUserProvider.loginByMobile(mobile);
    }

    public void checkLoginname(String loginname) throws BaseException {
        sysUserProvider.checkLoginname(loginname);
    }

    public void checkMobile(String mobile) throws BaseException {
        sysUserProvider.checkMobile(mobile);
    }

    public Sys_user getUserByLoginname(String loginname) throws BaseException {
        return sysUserProvider.getUserByLoginname(loginname);
    }

    public Sys_user getUserById(String id) throws BaseException {
        return sysUserProvider.getUserById(id);
    }

    public void setPwdByLoginname(String loginname, String password) throws BaseException {
        sysUserProvider.setPwdByLoginname(loginname, password);
    }

    public void setPwdById(String id, String password) throws BaseException {
        sysUserProvider.setPwdById(id, password);
    }
}
