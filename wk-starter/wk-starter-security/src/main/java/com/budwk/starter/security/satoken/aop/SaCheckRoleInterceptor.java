package com.budwk.starter.security.satoken.aop;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;

/**
 * @author wizzer@qq.com
 */
@IocBean(singleton = false)
public class SaCheckRoleInterceptor implements MethodInterceptor {
    private SaCheckRole at;

    public void prepare(SaCheckRole saCheckRole) {
        at = saCheckRole;
    }
    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        SaManager.getStpLogic(at.type()).checkByAnnotation(at);
        chain.doChain();
    }
}
