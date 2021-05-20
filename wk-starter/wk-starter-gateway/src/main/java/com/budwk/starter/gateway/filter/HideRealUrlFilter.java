package com.budwk.starter.gateway.filter;

import com.budwk.starter.gateway.context.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wentao
 * @author wizzer@qq.com
 */
public class HideRealUrlFilter implements RouteFilter, AutoCloseable {

    private static Log log = Logs.get();

    private String[] replaceKeys;

    private String name;

    @Override
    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        replaceKeys = Strings.splitIgnoreBlank(conf.get(prefix + ".hide_real_url_keys"));
        name = name + "HideRealUrlFilter";
    }

    @Override
    public boolean match(RouteContext ctx) {
        String requestUri = ctx.uri;
        // 只需要做关键词替换操作即可
        if(Lang.isNotEmpty(replaceKeys) && replaceKeys.length > 0) {
            for (String replaceKey : replaceKeys) {
                String[] keys = Strings.splitIgnoreBlank(replaceKey, "\\|");
                if(ctx.uri.startsWith("/" + keys[0])) {
                    ctx.uri = ctx.uri.replaceFirst(keys[0],keys[1]); // 只替换第一个关键词
                }
            }
        }
        log.debugf("request uri:[%s] -> realUri:[%s]", requestUri, ctx.uri);
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "hide-real-url";
    }

    @Override
    public void close() {
    }
}
