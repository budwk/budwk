package com.budwk.starter.gateway.filter;

import com.budwk.starter.gateway.context.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;

import java.io.IOException;

/**
 * @author wizzer@qq.com
 */
public interface RouteFilter {
    default boolean match(RouteContext ctx) {
        return true;
    };

    default boolean preRoute(RouteContext ctx) throws IOException {
        return true;
    }

    default void postRoute(RouteContext ctx) throws IOException {}

    default void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {}

    String getName();

    String getType();

    void close();
}
