package com.budwk.starter.common.openapi.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer(wizzer.cn)
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiFormParams {
    ApiFormParam[] value() default {};

    Class<?> implementation() default Void.class;

    String mediaType() default "application/json;charset=utf-8;";
}
