package com.budwk.app.sys.providers;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.starter.common.exception.BaseException;

import java.util.List;

/**
 * 系统用户
 *
 * @author wizzer@qq.com
 */
public interface ISysUserProvider {

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return
     */
    List<String> getPermissionList(String userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return
     */
    List<String> getRoleList(String userId);

    /**
     * 获取菜单列表
     *
     * @param userId 用户ID
     * @return
     */
    List<Sys_menu> getMenuList(String userId);

    /**
     * 获取菜单列表
     *
     * @param userId 用户ID
     * @param appId  应用ID
     * @return
     */
    List<Sys_menu> getMenuList(String userId, String appId);

    /**
     * 获取应用列表
     *
     * @param userId
     * @return
     */
    List<Sys_app> getAppList(String userId);

    /**
     * 检查用户是否存在
     *
     * @param loginname 用户名
     */
    void checkLoginname(String loginname) throws BaseException;

    /**
     * 检查用户是否存在
     *
     * @param mobile 手机号码
     */
    void checkMobile(String mobile) throws BaseException;

    /**
     * 通过用户名和密码获取用户信息
     *
     * @param loginname 用户名
     * @param passowrd  密码
     * @return
     * @throws BaseException
     */
    Sys_user loginByPassword(String loginname, String passowrd) throws BaseException;

    /**
     * 通过短信验证码登录
     *
     * @param mobile 手机号
     * @return
     * @throws BaseException
     */
    Sys_user loginByMobile(String mobile) throws BaseException;

    /**
     * 通过用户名获取用户信息
     *
     * @param loginname 用户名
     * @return
     * @throws BaseException
     */
    Sys_user getUserByLoginname(String loginname) throws BaseException;

    /**
     * 通过用户ID获取用户信息
     *
     * @param id 用户ID
     * @return
     * @throws BaseException
     */
    Sys_user getUserById(String id) throws BaseException;

    /**
     * 通过用户名重置密码
     *
     * @param loginname 用户名
     * @param password  新密码
     * @throws BaseException
     */
    void setPwdByLoginname(String loginname, String password) throws BaseException;

    /**
     * 通过用户ID重置密码
     *
     * @param id       用户ID
     * @param password 新密码
     * @throws BaseException
     */
    void setPwdById(String id, String password) throws BaseException;

    /**
     * 设置用户自定义样式
     *
     * @param id          用户ID
     * @param themeConfig 样式内容
     */
    void setThemeConfig(String id, String themeConfig);

    /**
     * 更新用户登录信息
     *
     * @param userId 用户ID
     * @param ip     IP地址
     */
    void setLoginInfo(String userId, String ip);
}
