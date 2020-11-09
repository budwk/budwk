package com.budwk.nb.app.commons.shiro.service;

import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.shop.commons.utils.JwtUtil;
import com.budwk.nb.sys.models.Sys_user;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/16
 */
@IocBean
public class JwtService {
    @Inject
    private RedisService redisService;
    private static int tokenExpireTime = 30;// 分钟

    /**
     * 获取新的token
     *
     * @return
     */
    public String refreshToken(String oldToken) {
        Sys_user sysUser = getUser(oldToken);
        //验证refreshToken是否有效
        if (sysUser!=null) {
            return loginForToken(sysUser);
        }
        return null;
    }

    public String loginForToken(Sys_user sysUser) {
        //获取用户token值
        String token = JwtUtil.sign(sysUser, tokenExpireTime * 60 * 1000);
        //将token作为RefreshToken Key 存到缓存中，缓存时间为token有效期的两倍
        redisService.setex(RedisConstant.REDIS_KEY_LOGIN_MEMBER_TOKEN_DATA + token, tokenExpireTime * 60 * 2, Json.toJson(sysUser, JsonFormat.compact()));
        return token;
    }

    public Sys_user getUser(String token) {
        String data = redisService.get(RedisConstant.REDIS_KEY_LOGIN_MEMBER_TOKEN_DATA + token);
        if (Strings.isNotBlank(data)) {
            return Json.fromJson(Sys_user.class, data);
        }
        return null;
    }
}
