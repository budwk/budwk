package com.budwk.starter.gateway.config;

import com.budwk.starter.gateway.route.Router;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.nacos.NacosConfigureLoader;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class RouteConfig {
    private static final Log log = Logs.get();

    public final static String PRE = "gateway.";

    public static final String PROP_GATEWAY_ROUTE_LOADNACOS = PRE + "route.load-nacos";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected AppContext appContext;

    protected List<Router> routers = new LinkedList<>();

    protected boolean blacklistEnable;

    protected List<String> blacklistIp = new ArrayList<>();

    public List<Router> getRouters() {
        return routers;
    }

    public boolean isBlacklistEnable() {
        return blacklistEnable;
    }

    public List<String> getBlacklistIp() {
        return blacklistIp;
    }

    public void reload() throws Exception {
        List<Router> routers = new LinkedList<>();
        for (String key : conf.getKeys()) {
            if (key.startsWith(PRE) && key.endsWith(".filter")) {
                String name = key.substring(PRE.length(), key.length() - ".filter".length());
                Router router = new Router();
                router.setAppContext(appContext);
                router.setPropertiesProxy(ioc, conf, PRE + name);
                routers.add(router);
            }
        }
        log.debugf("routers count=%d", routers.size());
        Collections.sort(routers);
        List<Router> oldMasters = this.routers;
        this.routers = routers;
        if (oldMasters != null && oldMasters.size() > 0) {
            for (Router router : oldMasters) {
                router.depose();
            }
        }
    }

    public void init() {
        try {
            reload();
            if (conf.getBoolean(PROP_GATEWAY_ROUTE_LOADNACOS, false)) {
                NacosConfigureLoader nacosConfigureLoader = ioc.get(NacosConfigureLoader.class);
                nacosConfigureLoader.addConfigListener(() -> {
                    try {
                        reload();
                    } catch (Exception e) {
                        log.error("fail to reload config!!!", e);
                    }
                });
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
