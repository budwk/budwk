package com.budwk.starter.gateway;

import com.budwk.starter.gateway.config.RouteConfig;
import com.budwk.starter.gateway.context.RouteContext;
import com.budwk.starter.gateway.http.HeaderMapRequestWrapper;
import com.budwk.starter.gateway.route.Router;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@IocBean
public class GatewayServletStarter extends AsyncMiddleManServlet implements WebServletFace {

    private static final Log log = Logs.get();

    protected static final String PRE = "gateway.";

    @PropDoc(value = "Gateway 是否从 nacos 配置中心同步路由规则", defaultValue = "false", type = "boolean")
    public static final String PROP_GATEWAY_ROUTE_LOADNACOS = PRE + "route.load-nacos";

    public static final String NAME_ROUTE_CONCEXT = PRE + "route_context";

    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected RouteConfig routeConfig;

    @Override
    protected void service(HttpServletRequest clientRequest, HttpServletResponse proxyResponse)
            throws ServletException, IOException {
        RouteContext ctx = new RouteContext();
        ctx.setup(clientRequest, proxyResponse);
        clientRequest.setAttribute(NAME_ROUTE_CONCEXT, ctx);
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(clientRequest);
        Router router = null;
        for (Router tmp : routeConfig.getRouters()) {
            if (tmp.match(ctx)) {
                router = tmp;
                break;
            }
        }
        if (router != null) {
            router.preRoute(ctx);
            if (ctx.respDone) {
                return;
            }
            super.service(requestWrapper, proxyResponse);
        } else {
            proxyResponse.sendError(404);
        }
    }

    @Override
    protected String rewriteTarget(HttpServletRequest clientRequest) {
        RouteContext ctx = ((RouteContext) clientRequest.getAttribute(NAME_ROUTE_CONCEXT));
        if (ctx.rewritedTarget != null)
            return ctx.rewritedTarget;
        String url = ctx.targetHost;
        if (url.startsWith("http://") || url.startsWith("https://")) {

        } else {
            url = "http://" + url;
        }
        if (ctx.targetPort > 0) {
            url += ":" + ctx.targetPort;
        }
        if (ctx.targetUri == null) {
            url += ctx.uri;
        } else {
            url += ctx.targetUri;
        }
        if (ctx.queryString != null) {
            url += "?" + ctx.queryString;
        }
        return url;
    }

    public String getName() {
        return "gateway";
    }

    public String getPathSpec() {
        return "/*";
    }

    public Servlet getServlet() {
        return this;
    }

    @Override
    public Map<String, String> getInitParameters() {
        Map<String, String> map = new HashMap<>();
        if (Strings.isNotBlank(conf.get(PRE + "http.maxThreads"))) {
            map.put("maxThreads", conf.get(PRE + "http.maxThreads"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.maxConnections"))) {
            map.put("maxConnections", conf.get(PRE + "http.maxConnections"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.idleTimeout"))) {
            map.put("idleTimeout", conf.get(PRE + "http.idleTimeout"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.timeout"))) {
            map.put("timeout", conf.get(PRE + "http.timeout"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.requestBufferSize"))) {
            map.put("requestBufferSize", conf.get(PRE + "http.requestBufferSize"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.responseBufferSize"))) {
            map.put("responseBufferSize", conf.get(PRE + "http.responseBufferSize"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.whiteList"))) {
            map.put("whiteList", conf.get(PRE + "http.whiteList"));
        }
        if (Strings.isNotBlank(conf.get(PRE + "http.blackList"))) {
            map.put("blackList", conf.get(PRE + "http.blackList"));
        }
        return map;
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }

    @Override
    protected void onProxyResponseFailure(HttpServletRequest clientRequest, HttpServletResponse proxyResponse,
                                          Response serverResponse, Throwable failure) {
        RouteContext ctx = (RouteContext) clientRequest.getAttribute(NAME_ROUTE_CONCEXT);
        try {
            ctx.respFail = true;
            ctx.router.postRoute(ctx);
            if (ctx.respDone)
                return;
        } catch (IOException e) {
            log.error("bad postRoute", e);
        }
        super.onProxyResponseFailure(clientRequest, proxyResponse, serverResponse, failure);
    }

    @Override
    protected void onProxyResponseSuccess(HttpServletRequest clientRequest, HttpServletResponse proxyResponse,
                                          Response serverResponse) {
        RouteContext ctx = (RouteContext) clientRequest.getAttribute(NAME_ROUTE_CONCEXT);
        try {
            ctx.respFail = false;
            ctx.router.postRoute(ctx);
            if (ctx.respDone)
                return;
        } catch (IOException e) {
            log.error("bad postRoute", e);
        }
        super.onProxyResponseSuccess(clientRequest, proxyResponse, serverResponse);
    }

    @Override
    public int getLoadOnStartup() {
        return 1;
    }
}
