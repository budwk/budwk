package com.budwk.starter.gateway.filter;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.budwk.starter.gateway.config.TargetServerInfo;
import com.budwk.starter.gateway.context.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wentao
 * @author wizzer@qq.com
 */
public class NacosServerSelectorFilter extends AbstractServerSelectorFilter implements EventListener {

    protected static final Log log = Logs.get();

    protected NamingService nacosNamingService;

    protected List<Instance> instances;

    @Override
    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        super.setPropertiesProxy(ioc, conf, prefix);
        if (Strings.isBlank(serviceName)) {
            throw new RuntimeException("nacos need service name!! prefix=" + prefix);
        }
        nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
        // 支持Group
        nacosNamingService.subscribe(serviceName, this);
        instances = nacosNamingService.selectInstances(serviceName, true);
        updateTargetServers(instances);
    }

    public void onEvent(Event event) {
        if (event instanceof NamingEvent) {
            instances = ((NamingEvent) event).getInstances();
            updateTargetServers(instances);
        }
    }

    protected void updateTargetServers(List<Instance> instances) {
        List<TargetServerInfo> infos = new ArrayList<TargetServerInfo>();
        for (Instance instance : instances) {
            TargetServerInfo info = new TargetServerInfo();
            info.host = instance.getIp();
            info.port = instance.getPort();
            infos.add(info);
        }
        super.targetServers = infos;
    }

    @Override
    protected boolean selectTargetServer(RouteContext ctx, List<TargetServerInfo> infos) {
        try {
            Instance ins = nacosNamingService.selectOneHealthyInstance(serviceName);
            if (ins != null) {
                ctx.targetHost = ins.getIp();
                ctx.targetPort = ins.getPort();
                return true;
            }
        } catch (NacosException e) {
            log.debug("selectOneHealthyInstance fail", e);
        }
        return false;
    }

    public String getType() {
        return "nacos";
    }

    @Override
    public void close() {
        if (nacosNamingService != null) {
            try {
                nacosNamingService.unsubscribe(serviceName, this);
            } catch (NacosException e) {
                log.warn("unsubscribe " + serviceName + "fail", e);
            }
        }
        super.close();
    }
}
