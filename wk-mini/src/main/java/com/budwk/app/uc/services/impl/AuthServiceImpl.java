package com.budwk.app.uc.services.impl;

import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.app.uc.services.AuthService;
import com.budwk.app.uc.services.ValidateService;
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
    private SysUserService sysUserService;

    public Sys_user loginByPassword(String loginname, String password,
                                    String key, String code) throws BaseException {
        validateService.checkCode(key, code);
        return sysUserService.loginByPassword(loginname, password);
    }

    public Sys_user loginByMobile(String mobile, String code) throws BaseException {
        validateService.checkSMSCode(mobile, code);
        return sysUserService.loginByMobile(mobile);
    }

    public void checkLoginname(String loginname) throws BaseException {
        sysUserService.checkLoginname(loginname);
    }

    public void checkMobile(String mobile) throws BaseException {
        sysUserService.checkMobile(mobile);
    }

    public Sys_user getUserByLoginname(String loginname) throws BaseException {
        return sysUserService.getUserByLoginname(loginname);
    }

    public Sys_user getUserById(String id) throws BaseException {
        return sysUserService.getUserById(id);
    }

    public void setPwdByLoginname(String loginname, String password) throws BaseException {
        sysUserService.setPwdByLoginname(loginname, password);
    }

    public void setPwdById(String id, String password) throws BaseException {
        sysUserService.setPwdById(id, password);
    }
}
