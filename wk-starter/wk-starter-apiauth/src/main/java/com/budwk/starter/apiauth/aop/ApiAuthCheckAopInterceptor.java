package com.budwk.starter.apiauth.aop;

import com.budwk.starter.apiauth.annotation.ApiAuthCheck;
import com.budwk.starter.apiauth.annotation.AuthMethod;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;

import java.lang.reflect.Method;
import java.util.Arrays;

@IocBean
@Slf4j
public class ApiAuthCheckAopInterceptor implements MethodInterceptor {
    @Override
    public void filter(InterceptorChain chain) throws Throwable {

        Method method = chain.getCallingMethod();
        ApiAuthCheck authCheck = method.getAnnotation(ApiAuthCheck.class);
        if (null == authCheck) {
            authCheck = method.getDeclaringClass().getAnnotation(ApiAuthCheck.class);
        }
        if (null == authCheck) {
            chain.doChain();
            return;
        }
        AuthMethod[] methods = authCheck.value();
        if (null == methods) {
            methods = new AuthMethod[]{AuthMethod.SIGN};
        }
        Object[] args = chain.getArgs();
        log.debug("methods：{}", Arrays.toString(methods));
        log.debug("参数长度：{}", args.length);
        log.debug("请求地址：{}", Mvcs.getReq().getRequestURL());
    }
}
