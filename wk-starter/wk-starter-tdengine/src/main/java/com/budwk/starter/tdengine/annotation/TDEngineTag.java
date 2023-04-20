package com.budwk.starter.tdengine.annotation;

import java.lang.annotation.*;

/**
 * @author wizzer@qq.com
 * @author caoshi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface TDEngineTag {
}
