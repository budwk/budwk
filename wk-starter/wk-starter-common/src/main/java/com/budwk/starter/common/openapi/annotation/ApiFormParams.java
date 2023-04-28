package com.budwk.starter.common.openapi.annotation;

import com.budwk.starter.common.openapi.enums.DataType;

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

    /**
     * 数据类型 object||array
     * @return
     */
    String dataType() default "object";
}
