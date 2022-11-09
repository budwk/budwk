package com.budwk.starter.gateway;

import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.gateway.config.RouteConfig;
import com.budwk.starter.gateway.context.RouteContext;
import com.budwk.starter.gateway.http.HeaderMapRequestWrapper;
import com.budwk.starter.gateway.route.Router;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.slf4j.MDC;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
