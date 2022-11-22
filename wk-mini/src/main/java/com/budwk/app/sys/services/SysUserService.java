package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysUserService extends BaseService<Sys_user> {

    Sys_user_security getUserSecurity();

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return
     */
    List<String> getPermissionList(String userId);

    /**
     * 获取用户角色
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
     * 通过用户ID获取菜单及权限
     *
     * @param userId 用户ID
     * @param appId  应用ID
     * @return
     */
    List<Sys_menu> getMenusAndDatas(String userId, String appId);


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
     * 检查密码是否过期
     *
     * @param userId     用户ID
     * @param pwdResetAt 密码重置时间
     */
    void checkPwdTimeout(String userId, Long pwdResetAt) throws BaseException;

    /**
     * 密码规则校验
     *
     * @param user 用户对象
     * @param pwd  密码
     * @throws BaseException
     */
    void checkPassword(Sys_user user, String pwd) throws BaseException;

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

    /**
     * 新增用户(密码未设置则取手机号后6位)
     *
     * @param user    用户对象
     * @param roleIds 角色ID数组
     */
    void create(Sys_user user, String[] roleIds);

    /**
     * 修改用户
     *
     * @param user    用户对象
     * @param roleIds 角色ID数组
     */
    void update(Sys_user user, String[] roleIds);

    /**
     * 重置用户密码为随机6位数字
     *
     * @param userId 用户ID
     * @return
     */
    String resetPwd(String userId);

    /**
     * 重置用户密码
     *
     * @param userId        用户ID
     * @param password      新密码
     * @param needChangePwd 是否需要登录修改密码
     * @return
     */
    String resetPwd(String userId, String password, boolean needChangePwd);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(String userId);

    /**
     * 导入用户数据
     *
     * @param userList        用户列表
     * @param password        默认密码
     * @param isUpdateSupport 是否更新已有数据
     * @param userId          操作人ID
     * @return
     */
    String importUser(List<Sys_user> userList, String password, Boolean isUpdateSupport, String userId);

    /**
     * 删除用户缓存
     *
     * @param userId 用户ID
     */
    void cacheRemove(String userId);

    /**
     * 清空用户缓存
     */
    void cacheClear();
}
