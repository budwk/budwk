package com.budwk.app.ucenter.services.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.providers.ISysUserProvider;
import com.budwk.app.ucenter.services.AuthService;
import com.budwk.app.ucenter.services.ValidateService;
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
    @Reference(check = false)
    private ISysUserProvider sysUserProvider;

    @Inject
    private RedisService redisService;

    // 等待时间(s)
    private final static int CHECK_WAIT_TIME = 20 * 60;
    // 用户名次数
    private final static int CHECK_COUNT = 3;

    public Sys_user loginByPassword(String loginname, String password,
                                    String key, String code) throws BaseException {
        validateService.checkCode(key, code);
        return sysUserProvider.loginByPassword(loginname, password);
    }

    public Sys_user loginByMobile(String mobile, String code) throws BaseException {
        validateService.checkSMSCode(mobile, code);
        return sysUserProvider.loginByMobile(mobile);
    }

    public void checkLoginname(String loginname, String ip) throws BaseException {
        int count = Integer.parseInt(Strings.sNull(redisService.get(RedisConstant.PRE + "checkLoginname:" + ip), "0"));
        if (count > CHECK_COUNT - 1) {
            long ttl = redisService.ttl(RedisConstant.PRE + "checkLoginname:" + ip);
            long m = ttl / 60;
            long s = ttl % 60;
            throw new BaseException(String.format("您的IP已被锁定，请%s分钟%s秒后重试", m, s));
        }
        try {
            sysUserProvider.checkLoginname(loginname);
        } catch (BaseException baseException) {
            if (baseException.getMessage().equals("用户名不存在")) {
                redisService.setex(RedisConstant.PRE + "checkLoginname:" + ip, CHECK_WAIT_TIME, String.valueOf(++count));
            }
            throw baseException;
        }
    }

    public void checkMobile(String mobile, String ip) throws BaseException {
        int count = Integer.parseInt(Strings.sNull(redisService.get(RedisConstant.PRE + "checkMobile:" + ip), "0"));
        if (count > CHECK_COUNT - 1) {
            long ttl = redisService.ttl(RedisConstant.PRE + "checkMobile:" + ip);
            long m = ttl / 60;
            long s = ttl % 60;
            throw new BaseException(String.format("您的IP已被锁定，请%s分钟%s秒后重试", m, s));
        }
        try {
            sysUserProvider.checkMobile(mobile);
        } catch (BaseException baseException) {
            if (baseException.getMessage().equals("手机号不存在")) {
                redisService.setex(RedisConstant.PRE + "checkMobile:" + ip, CHECK_WAIT_TIME, String.valueOf(++count));
            }
            throw baseException;
        }
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
