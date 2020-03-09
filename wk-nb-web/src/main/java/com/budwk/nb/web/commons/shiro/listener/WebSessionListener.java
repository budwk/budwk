package com.budwk.nb.web.commons.shiro.listener;

import com.budwk.nb.commons.constants.RedisConstant;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

/**
 * session动作监听
 * @author wizzer(wizzer.cn) on 2018/7/5.
 */
@IocBean
public class WebSessionListener implements SessionListener {
    @Inject
    private RedisService redisService;

    @Override
    public void onStart(Session session) {//会话创建触发 已进入shiro过滤器的会话就触发这个方法

    }

    @Override
    public void onStop(Session session) {//退出
        try {
            String session_userToken = Strings.sNull(session.getAttribute("userToken"));
            String session_userId = Strings.sNull(session.getAttribute("userId"));
            if (Strings.isNotBlank(session_userToken) && Strings.isNotBlank(session_userId)) {
                redisService.del(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + session_userId + ":" + session_userToken);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onExpiration(Session session) {//会话过期时触发
        try {
            String session_userToken = Strings.sNull(session.getAttribute("userToken"));
            String session_userId = Strings.sNull(session.getAttribute("userId"));
            if (Strings.isNotBlank(session_userToken) && Strings.isNotBlank(session_userId)) {
                redisService.del(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + session_userId + ":" + session_userToken);
            }
        } catch (Exception e) {

        }
    }

}
