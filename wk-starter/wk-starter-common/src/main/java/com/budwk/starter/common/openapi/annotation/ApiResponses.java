package com.budwk.starter.common.openapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wizzer@qq.com
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponses {
    /**
     * Result.data 返回的变量定义
     * @return
     */
    ApiResponse[] value() default {};

    /**
     * Result.data 返回的对象,支持带 @ApiModel 的类的关联
     * @return
     */
    Class<?> implementation() default Void.class;

    String mediaType() default "application/json;charset=utf-8;";

    /**
     * 必须为json格式字符串
     * @return
     */
    String example() default "";
}
