package com.budwk.starter.gateway.filter;

import com.budwk.starter.gateway.config.TargetServerInfo;
import com.budwk.starter.gateway.context.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
public abstract class AbstractServerSelectorFilter implements RouteFilter, AutoCloseable {

    private static final Log log = Logs.get();

    protected String name;

    protected String serviceName;

    protected List<TargetServerInfo> targetServers;

    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        this.name = prefix;
        serviceName = conf.get(prefix + ".serviceName");
    }

    @Override
    public boolean preRoute(RouteContext ctx) throws IOException {
        if (!selectTargetServer(ctx, this.targetServers)) {
            log.debugf("emtry server list for [%s]", serviceName);
            ctx.resp.sendError(500);
            return false; // 终止匹配
        }

        return true;
    }

    protected boolean selectTargetServer(RouteContext ctx, List<TargetServerInfo> infos) {
        if (infos == null || infos.isEmpty())
            return false;
        int index = R.random(0, infos.size() - 1); // 支持各种算法
        if (index == infos.size()) {
            index = infos.size() - 1;
        }
        TargetServerInfo info = infos.get(index);
        ctx.targetHost = info.host;
        if (info.port > 0)
            ctx.targetPort = info.port;
        if (log.isDebugEnabled())
            log.debugf("forward reqest %s to %s:%s", ctx.uri, ctx.targetHost, ctx.targetPort);
        return true;
    }

    public String getName() {
        return name;
    }

    public void close() {

    }
}
