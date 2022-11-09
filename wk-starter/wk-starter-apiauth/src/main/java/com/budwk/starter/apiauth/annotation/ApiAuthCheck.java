package com.budwk.starter.apiauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAuthCheck {
    /**
     * 接口校验的方法
     *
     * @return
     */
    AuthMethod[] value() default AuthMethod.SIGN;
}
