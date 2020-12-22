package com.budwk.nb.web.commons.processor;

import com.budwk.nb.base.constant.RedisConstant;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * userToken处理,转给shiro来判断请求权限
 * @author wizzer(wizzer.cn) on 2019/10/29
 */
public class WkUserTokenProcessor extends AbstractProcessor {
    private static final Log log = Logs.get();
    private RedisService redisService;
    private PropertiesProxy conf;
    private int REDIS_KEY_SESSION_TTL;

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        redisService = config.getIoc().get(RedisService.class);
        conf = config.getIoc().get(PropertiesProxy.class, "conf");
        REDIS_KEY_SESSION_TTL = conf.getInt("shiro.session.cache.redis.ttl");
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        String userToken = ac.getRequest().getHeader("X-Token");
        String userId = ac.getRequest().getHeader("X-Id");
        String nowSessionId = ac.getRequest().getSession().getId();
        try {
            if (Strings.isNotBlank(userToken) && Strings.isNotBlank(userId)) {
                String sessionId = redisService.get(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + userId + ":" + userToken);
                if (Strings.isNotBlank(sessionId) && !nowSessionId.equals(sessionId)) {
                    //通过token获取session,绑定到shiro
                    Subject.Builder builder = new Subject.Builder(SecurityUtils.getSecurityManager());
                    builder.sessionId(sessionId);
                    ThreadContext.bind(builder.buildSubject());
                    nowSessionId = sessionId;
                }
            }
            // 更新redis 统计在线状态
            String session_userToken = Strings.sNull(ac.getRequest().getSession().getAttribute("userToken"));
            String session_userId = Strings.sNull(ac.getRequest().getSession().getAttribute("userId"));
            if (Strings.isNotBlank(session_userToken) && Strings.isNotBlank(session_userId)) {
                redisService.setex(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + session_userId + ":" + session_userToken, REDIS_KEY_SESSION_TTL, nowSessionId);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        doNext(ac);
    }
}
