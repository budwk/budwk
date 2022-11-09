package com.budwk.starter.gateway.ws;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.budwk.starter.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.nutz.lang.Lang;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@Slf4j
public class WsServlet extends WebSocketProxyServlet {
    protected NamingService nacosNamingService;

    protected String serviceName;

    protected String group;

    protected String url;

    protected boolean blacklistEnable;

    protected List<String> blacklistIp;

    public WsServlet(NamingService nacosNamingService, String serviceName, String group, String url, boolean blacklistEnable, List<String> blacklistIp) {
        this.nacosNamingService = nacosNamingService;
        this.serviceName = serviceName;
        this.group = group;
        this.url = url;
        this.blacklistEnable = blacklistEnable;
        this.blacklistIp = blacklistIp;
    }

    @Override
    protected URI redirect(ServletUpgradeRequest request) {
        if (blacklistEnable) {
            String ip = Lang.getIP(request.getHttpServletRequest());
            if (blacklistIp.contains(ip)) {
                log.info("WS request from blacklist ip : " + ip);
                return null;
            }
        }
        String redirectUrl = "";
        try {
            Instance ins = nacosNamingService.selectOneHealthyInstance(serviceName, group);
            if (ins != null) {
                redirectUrl = "ws://" + ins.getIp() + ":" + ins.getPort() + url;
            }
        } catch (NacosException e) {
            log.error(e.getErrMsg());
            throw new BaseException("NacosException");
        }
        try {
            log.info("WS redirectUrl==" + redirectUrl);
            return new URI(redirectUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
