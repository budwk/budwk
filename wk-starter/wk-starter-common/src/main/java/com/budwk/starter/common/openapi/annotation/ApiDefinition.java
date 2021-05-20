package com.budwk.starter.common.openapi.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiDefinition {
    String tag() default "";
}
