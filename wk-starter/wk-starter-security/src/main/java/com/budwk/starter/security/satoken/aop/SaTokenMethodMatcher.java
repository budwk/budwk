package com.budwk.starter.security.satoken.aop;

import org.nutz.aop.MethodMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author wizzer@qq.com
 */
public class SaTokenMethodMatcher implements MethodMatcher {

    protected Class<? extends Annotation> klass;

    public SaTokenMethodMatcher(Class<? extends Annotation> klass) {
        this.klass = klass;
    }

    public boolean match(Method method) {
        return method.getAnnotation(klass) != null;
    }

}
