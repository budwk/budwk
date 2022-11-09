package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.app.sys.services.SysUserSecurityService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.exception.BaseException;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = ISysUserProvider.class)
@IocBean
public class SysUserProvider implements ISysUserProvider {
    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysUserSecurityService sysUserSecurityService;

    @Override
    public Sys_user_security getUserSecurity() {
        return sysUserSecurityService.getWithCache();
    }

    @Override
    public List<String> getPermissionList(String userId) {
        return sysUserService.getPermissionList(userId);
    }

    @Override
    public List<String> getRoleList(String userId) {
        return sysUserService.getRoleList(userId);
    }

    @Override
    public List<Sys_menu> getMenuList(String userId) {
        return sysUserService.getMenuList(userId);
    }

    @Override
    public List<Sys_menu> getMenuList(String userId, String appId) {
        return sysUserService.getMenuList(userId, appId);
    }

    @Override
    public List<Sys_app> getAppList(String userId) {
        return sysUserService.getAppList(userId);
    }

    @Override
    public void checkLoginname(String loginname) throws BaseException {
        sysUserService.checkLoginname(loginname);
    }

    @Override
    public void checkMobile(String mobile) throws BaseException {
        sysUserService.checkMobile(mobile);
    }

    @Override
    public void checkPwdTimeout(String userId, Long pwdResetAt) throws BaseException {
        sysUserService.checkPwdTimeout(userId, pwdResetAt);
    }

    @Override
    public Sys_user loginByPassword(String loginname, String passowrd) throws BaseException {
        return sysUserService.loginByPassword(loginname, passowrd);
    }

    @Override
    public Sys_user loginByMobile(String mobile) throws BaseException {
        return sysUserService.loginByMobile(mobile);
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

    public void setThemeConfig(String id, String themeConfig) {
        sysUserService.setThemeConfig(id, themeConfig);
    }

    public void setLoginInfo(String userId, String ip) {
        sysUserService.setLoginInfo(userId, ip);
    }
}
