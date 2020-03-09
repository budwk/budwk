package com.budwk.nb.web.commons.processor;

import com.budwk.nb.commons.base.Result;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.integration.shiro.NutShiroInterceptor;
import org.nutz.integration.shiro.NutShiroMethodInterceptor;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * Shiro权限验证
 * @author wizzer(wizzer.cn) on 2016/6/24.
 */
public class WkShiroProcessor extends AbstractProcessor {

    protected NutShiroMethodInterceptor interceptor;

    protected boolean match;

    protected boolean init;

    public WkShiroProcessor() {
        interceptor = new NutShiroMethodInterceptor();
    }

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        // 禁止重复初始化,常见于ioc注入且使用了单例
        if (init)
        {
            throw new IllegalStateException("this Processor have bean inited!!");
        }
        super.init(config, ai);
        match = NutShiro.match(ai.getMethod());
        init = true;
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (match) {
            try {
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