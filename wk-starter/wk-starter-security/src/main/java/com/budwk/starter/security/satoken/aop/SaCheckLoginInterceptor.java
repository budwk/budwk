package com.budwk.starter.security.satoken.aop;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(singleton = false)
public class SaCheckLoginInterceptor implements MethodInterceptor {
    private SaCheckLogin at;

    public void prepare(SaCheckLogin saCheckLogin) {
        at = saCheckLogin;
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        SaManager.getStpLogic(at.type()).checkByAnnotation(at);
        chain.doChain();
    }
}
