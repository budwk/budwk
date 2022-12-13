package com.budwk.starter.common.openapi.annotation;

import com.budwk.starter.common.enums.Validation;

import java.lang.annotation.*;

/**
 * @author wizzer(wizzer.cn)
 */
@Target(ElementType.PARAMETER)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiFormParam {
    String name() default "";

    String type() default "string";

    String description() default "";

    String example() default "";

    /**
     * 是否必填(接口文档与表单验证共用)
     * @return
     */
    boolean required() default false;

    String df() default "";

    /**
     * 表单验证是否启用
     *
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
     * @return
     */
    String regex() default "";

    /**
     * 表单验证失败提示消息
     * @return
     */
    String msg() default "";
}
