package com.budwk.starter.gateway.context;

import com.budwk.starter.gateway.route.Router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
public class RouteContext {
    public String method;
    public String uri;
    public String host;
    public Map<String, String> headers;
    public String queryString;

    public String targetHost;
    public String targetUri;
    public int targetPort;
    public String matchedPrefix;
    public String serviceName;

    public String rewritedTarget;

    public int connectTimeOut, sendTimeOut, readTimeOut;

    public Object obj;

    public HttpServletResponse resp;
    public HttpServletRequest req;

    public Router router;
    public boolean respFail;
    public boolean respDone;

    public void setup(HttpServletRequest req, HttpServletResponse resp) {
        method = req.getMethod().toUpperCase();
        uri = req.getRequestURI();
        host = req.getHeader("Host");
        headers = new HashMap<>();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, req.getHeader(headerName));
        }
        queryString = req.getQueryString();
        this.req = req;
        this.resp = resp;
    }
}
