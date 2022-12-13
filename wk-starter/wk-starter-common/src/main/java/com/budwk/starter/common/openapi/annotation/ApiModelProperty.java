package com.budwk.starter.common.openapi.annotation;

import com.budwk.starter.common.enums.Validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * type 不用定义,会根据字段类型自动获取
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiModelProperty {
    String name() default "";

    String description() default "";

    /**
     * 是否必填(接口文档与表单验证共用)
     * @return
     */
    boolean required() default false;

    String example() default "";

    String df() default "";

    /**
     * true=表单参数,false=不在表单请求里显示
     * @return
     */
    boolean param() default true;

    /**
     * 表单验证是否启用
     * @return
     */
    boolean check() default false;

    /**
     * 表单验证正则(枚举)
     * @return
     */
    Validation validation() default Validation.NONE;

    /**
     * 表单验证正则(自定义)
     */
    String regex() default "";

    /**
     * 表单验证失败提示消息
     */
    String msg() default "";
}
