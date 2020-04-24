package com.budwk.nb.web.commons.processor;

import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.web.commons.utils.JwtUtil;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.integration.shiro.NutShiroInterceptor;
import org.nutz.integration.shiro.NutShiroMethodInterceptor;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * Shiro权限验证
 *
 * @author wizzer(wizzer @ qq.com) on 2016/6/24.
 */
public class WkShiroProcessor extends AbstractProcessor {
    private static final Log log = Logs.get();
    private RedisService redisService;
    private PropertiesProxy conf;
    private int REDIS_KEY_SESSION_TTL;
    private ShiroUtil shiroUtil;
    protected NutShiroMethodInterceptor interceptor;

    protected boolean match;

    protected boolean init;

    public WkShiroProcessor() {
        interceptor = new NutShiroMethodInterceptor();
    }

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        // 禁止重复初始化,常见于ioc注入且使用了单例
        if (init) {
            throw new IllegalStateException("this Processor have bean inited!!");
        }
        redisService = config.getIoc().get(RedisService.class);
        conf = config.getIoc().get(PropertiesProxy.class, "conf");
        shiroUtil = config.getIoc().get(ShiroUtil.class);
        REDIS_KEY_SESSION_TTL = conf.getInt("shiro.session.cache.redis.ttl");
        super.init(config, ai);
        match = NutShiro.match(ai.getMethod());
        init = true;
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (match) {
            String userToken = ac.getRequest().getHeader("X-Token");
            String nowSessionId = ac.getRequest().getSession().getId();
            try {
                String userId = JwtUtil.getUserId();
                // 验证签名
                if (userId == null) {
                    throw Lang.makeThrow(UnauthenticatedException.class, "Token is error!");
                }
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
                Sys_user sysUser = (Sys_user) shiroUtil.getPrincipal();
                if (sysUser != null) {
                    // 签名超时失效 刷新签名
                    if (!JwtUtil.verify(userToken, sysUser.getPassword())) {
                        ac.getResponse().setHeader("token", JwtUtil.sign(sysUser, REDIS_KEY_SESSION_TTL * 1000));
                    }
                }
                // 验证shiro权限,是否登陆等
                interceptor.assertAuthorized(new NutShiroInterceptor(ac));
            } catch (Exception e) {
                whenException(ac, e);
                return;
            }
        }
        doNext(ac);
    }

    protected void whenException(ActionContext ac, Exception e) throws Throwable {
        Object val = ac.getRequest().getAttribute("shiro_auth_error");
        if (val != null && val instanceof View) {
            ((View) val).render(ac.getRequest(), ac.getResponse(), null);
            return;
        }
        if (e instanceof UnauthenticatedException) {
            whenUnauthenticated(ac, (UnauthenticatedException) e);
        } else if (e instanceof UnauthorizedException) {
            whenUnauthorized(ac, (UnauthorizedException) e);
        } else {
            whenOtherException(ac, e);
        }
    }

    protected void whenUnauthenticated(ActionContext ac, UnauthenticatedException e) throws Exception {
        NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(500208, "system.login.accessdenied"));
    }

    protected void whenUnauthorized(ActionContext ac, UnauthorizedException e) throws Exception {
        NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(500209, "system.login.unauthorized"));
    }

    protected void whenOtherException(ActionContext ac, Exception e) throws Exception {
        NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(500210, "system.login.accessdenied"));
    }
}