package com.budwk.starter.common.openapi.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiModel {
    String name() default "";

    String description() default "";
}
