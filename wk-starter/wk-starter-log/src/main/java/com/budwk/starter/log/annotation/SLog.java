package com.budwk.starter.log.annotation;

import com.budwk.starter.log.enums.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wizzer@qq.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SLog {
    /**
     * 日志类型
     *
     * @return
     */
    LogType type() default LogType.ACTION;

    /**
     * 日志标签
     *
     * @return
     */
    String tag() default "";

    /**
     * 日志内容
     *
     * @return
     */
    String value() default "";

    /**
     * 是否记录传递参数
     *
     * @return
     */
    boolean param() default true;

    /**
     * 是否记录执行结果
     *
     * @return
     */
    boolean result() default true;

}
