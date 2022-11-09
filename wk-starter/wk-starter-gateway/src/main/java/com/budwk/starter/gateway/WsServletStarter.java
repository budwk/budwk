package com.budwk.starter.gateway;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingService;
import com.budwk.starter.gateway.ws.WsServlet;
import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
@Slf4j
public class WsServletStarter implements WebServletFace {

    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;

    public final static String PRE = "gateway.";

    public static final String PROP_GATEWAY_BLACKLIST_ENABLE = PRE + "blacklist.enable";

    public static final String PROP_GATEWAY_BLACKLIST_IP = PRE + "blacklist.ip";

    protected NamingService nacosNamingService;

    protected String serviceName;

    protected String group;

    protected String url;

    protected boolean blacklistEnable;

    protected List<String> blacklistIp = new ArrayList<>();

    public void init() {
        nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
        url = conf.get("gateway.websocket.url", "/platform/websocket");
        group = conf.get("gateway.websocket.group", Constants.DEFAULT_GROUP);
        serviceName = conf.get("gateway.websocket.serviceName", "");
        blacklistEnable = conf.getBoolean(PROP_GATEWAY_BLACKLIST_ENABLE, false);
        blacklistIp = conf.getList(PROP_GATEWAY_BLACKLIST_IP);
    }

    @Override
    public String getName() {
        return "websocket";
    }

    @Override
    public String getPathSpec() {
        return "/websocket";
    }

    @Override
    public Servlet getServlet() {
        return new WsServlet(nacosNamingService, serviceName, group, url, blacklistEnable, blacklistIp);
    }

    @Override
    public int getLoadOnStartup() {
        return 2;
    }
}
