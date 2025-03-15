package com.budwk.starter.rediscache.annotation;

import java.lang.annotation.*;

/**
 * Created by wizzer on 2017/6/14.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheUpdate {
    String cacheName() default "";

    String cacheKey() default "";

    int cacheLiveTime() default 0;
}
