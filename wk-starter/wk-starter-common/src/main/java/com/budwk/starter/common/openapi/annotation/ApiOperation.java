package com.budwk.starter.common.openapi.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer@qq.com
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiOperation {
    String name() default "";

    String description() default "";
}
