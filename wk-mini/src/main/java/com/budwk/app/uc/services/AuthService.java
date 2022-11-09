package com.budwk.app.uc.services;


import com.budwk.app.sys.models.Sys_user;
import com.budwk.starter.common.exception.BaseException;

/**
 * 认证服务
 *
 * @author wizzer@qq.com
 */
public interface AuthService {
    /**
     * 通过用户密码登录
     *
     * @param loginname 用户名
     * @param password  密码
     * @param key       验证码key
     * @param code      验证码
     * @return
     * @throws BaseException
     */
    Sys_user loginByPassword(String loginname, String password,
                             String key, String code) throws BaseException;

    /**
     * 通过短信验证码登录
     *
     * @param mobile 手机号码
     * @param code   短信验证码
     * @return
     * @throws BaseException
     */
    Sys_user loginByMobile(String mobile, String code) throws BaseException;

    /**
     * 检查用户是否存在
     *
     * @param loginname 用户名
     */
    void checkLoginname(String loginname, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException;

    /**
     * 检查用户是否存在
     *
     * @param mobile 手机号码
     */
    void checkMobile(String mobile, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException;

    /**
     * 通过用户名获取用户信息
     *
     * @param loginname 用户名
     * @return
     * @throws BaseException
     */
    Sys_user getUserByLoginname(String loginname, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException;


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
}
