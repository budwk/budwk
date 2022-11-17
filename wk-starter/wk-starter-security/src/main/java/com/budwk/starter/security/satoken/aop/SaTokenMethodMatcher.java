package com.budwk.starter.security.satoken.aop;

import org.nutz.aop.MethodMatcher;

import java.lang.reflect.Method;

/**
 * @author wizzer@qq.com
 */
public class SaTokenMethodMatcher implements MethodMatcher {

    protected Method method;

    public SaTokenMethodMatcher(Method method) {
        this.method = method;
    }

    public boolean match(Method method) {
        return this.method.equals(method);
    }

}
