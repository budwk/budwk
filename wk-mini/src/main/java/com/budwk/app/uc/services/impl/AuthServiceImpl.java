package com.budwk.app.uc.services.impl;

import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.app.uc.services.AuthService;
import com.budwk.app.uc.services.ValidateService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.BaseException;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

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

    @Inject
    private RedisService redisService;

    // 等待时间(s)
    private final static int CHECK_WAIT_TIME = 20 * 60;
    // 用户名次数
    private final static int CHECK_COUNT = 3;

    public Sys_user loginByPassword(String loginname, String password,
                                    String key, String code) throws BaseException {
        Sys_user_security security = sysUserService.getUserSecurity();
        if (security != null && security.getCaptchaHasEnabled() != null && security.getCaptchaHasEnabled()) {
            validateService.checkCode(key, code);
        }
        return sysUserService.loginByPassword(loginname, password);
    }

    public Sys_user loginByMobile(String mobile, String code) throws BaseException {
        validateService.checkSMSCode(mobile, code);
        return sysUserService.loginByMobile(mobile);
    }

    public void checkLoginname(String loginname, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException {
        if (nameRetryLock) {
            int count = Integer.parseInt(Strings.sNull(redisService.get(RedisConstant.PRE + "checkLoginname:" + ip), "0"));
            if (count > nameRetryNum - 1) {
                long ttl = redisService.ttl(RedisConstant.PRE + "checkLoginname:" + ip);
                long m = ttl / 60;
                long s = ttl % 60;
                throw new BaseException(String.format("您的IP已被锁定，请%s分钟%s秒后重试", m, s));
            }
            try {
                sysUserService.checkLoginname(loginname);
            } catch (BaseException baseException) {
                if (baseException.getMessage().equals("用户名不存在")) {
                    redisService.setex(RedisConstant.PRE + "checkLoginname:" + ip, nameTimeout, String.valueOf(++count));
                }
                throw baseException;
            }
        } else {
            sysUserService.checkLoginname(loginname);
        }
    }

    public void checkMobile(String mobile, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException {
        if (nameRetryLock) {
            int count = Integer.parseInt(Strings.sNull(redisService.get(RedisConstant.PRE + "checkMobile:" + ip), "0"));
            if (count > nameRetryNum - 1) {
                long ttl = redisService.ttl(RedisConstant.PRE + "checkMobile:" + ip);
                long m = ttl / 60;
                long s = ttl % 60;
                throw new BaseException(String.format("您的IP已被锁定，请%s分钟%s秒后重试", m, s));
            }
            try {
                sysUserService.checkMobile(mobile);
            } catch (BaseException baseException) {
                if (baseException.getMessage().equals("手机号不存在")) {
                    redisService.setex(RedisConstant.PRE + "checkMobile:" + ip, nameTimeout, String.valueOf(++count));
                }
                throw baseException;
            }
        } else {
            sysUserService.checkMobile(mobile);
        }
    }

    public Sys_user getUserByLoginname(String loginname, String ip, boolean nameRetryLock, int nameRetryNum, int nameTimeout) throws BaseException {
        if (nameRetryLock) {
            int count = Integer.parseInt(Strings.sNull(redisService.get(RedisConstant.PRE + "getUserByLoginname:" + ip), "0"));
            if (count > nameRetryNum - 1) {
                long ttl = redisService.ttl(RedisConstant.PRE + "getUserByLoginname:" + ip);
                long m = ttl / 60;
                long s = ttl % 60;
                throw new BaseException(String.format("您的IP已被锁定，请%s分钟%s秒后重试", m, s));
            }
            try {
                return sysUserService.getUserByLoginname(loginname);
            } catch (BaseException baseException) {
                if (baseException.getMessage().equals("用户不存在")) {
                    redisService.setex(RedisConstant.PRE + "getUserByLoginname:" + ip, nameTimeout, String.valueOf(++count));
                }
                throw baseException;
            }
        } else {
            return sysUserService.getUserByLoginname(loginname);
        }
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
