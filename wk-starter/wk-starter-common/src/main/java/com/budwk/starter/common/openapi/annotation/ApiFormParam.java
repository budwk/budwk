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
     * 是否必填与表单验证共用
     * @return
     */
    boolean required() default false;

    String df() default "";

    /**
     * starter-validation 是否启用表单验证
     *
     * @return
     */
    boolean check() default false;

    /**
     * starter-validation 正则验证枚举
     * @return
     */
    Validation validation() default Validation.NONE;

    /**
     * starter-validation 表单验证正则规则
     * @return
     */
    String regex() default "";

    /**
     * starter-validation 表单验证失败提示消息
     * @return
     */
    String msg() default "";
}
