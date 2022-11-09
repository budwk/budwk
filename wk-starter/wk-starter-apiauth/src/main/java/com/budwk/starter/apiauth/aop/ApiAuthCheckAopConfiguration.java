package com.budwk.starter.apiauth.aop;

import com.budwk.starter.apiauth.annotation.ApiAuthCheck;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.List;

@IocBean(name = "$aop_wkApiAuthCheck")
public class ApiAuthCheckAopConfiguration extends SimpleAopMaker<ApiAuthCheck> {
    @Override
    public List<? extends MethodInterceptor> makeIt(ApiAuthCheck apiSignCheck, Method method, Ioc ioc) {
        return List.of(ioc.get(ApiAuthCheckAopInterceptor.class));
    }
}
