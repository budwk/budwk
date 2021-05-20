package com.budwk.starter.gateway.filter;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.budwk.starter.gateway.context.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wentao
 * @author wizzer@qq.com
 */
public class RewriteUrlFilter implements RouteFilter, AutoCloseable {

    protected static final Log log = Logs.get();

    private static final String URI = ".uri";
    private static final String TARGET_URI = ".targetUri";
    private static final String SERVICE_NAME = ".serviceName";
    protected NamingService nacosNamingService;
    private String name = "";
    private String uri = "";
    private String targetUri = "";
    private String serviceName = "";


    @Override
    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        this.name = prefix + "-RewriteFilter";
        this.uri = conf.get(prefix + URI);
        this.targetUri = conf.get(prefix + TARGET_URI);
        this.serviceName = conf.get(prefix + SERVICE_NAME);
        this.nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
    }

    @Override
    public boolean match(RouteContext ctx) {
        try {
            if (ctx.uri.equals(this.uri)) {
                Instance healthyInstance = this.nacosNamingService.selectOneHealthyInstance(this.serviceName);
                String oldTargetHost = ctx.targetHost;
                int oldTargetPort = ctx.targetPort;
                String oldUri = ctx.uri;
                ctx.targetHost = healthyInstance.getIp();
                ctx.targetPort = healthyInstance.getPort();
                ctx.uri = this.targetUri;
                log.debugf("转发请求[%s:%s%s] => [%s:%s%s]", oldTargetHost, oldTargetPort, oldUri, ctx.targetHost, ctx.targetPort, ctx.uri);
                return true;
            }
        } catch (NacosException e) {
            log.debug("selectOneHealthyInstance fail", e);
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "rewrite-url";
    }

    @Override
    public void close() {

    }
}
