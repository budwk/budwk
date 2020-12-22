package com.budwk.nb.web.commons.ext.pubsub;

import com.budwk.nb.base.constant.RedisConstant;
import com.budwk.nb.sys.services.SysConfigService;
import com.budwk.nb.web.commons.base.Globals;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 订阅发布用于更新所有实例的 Globals变量
 *
 * @author wizzer(wizzer.cn) on 2018/3/18.
 */
@IocBean(create = "init")
public class WebPubSub implements PubSub {
    private static final Log log = Logs.get();
    @Inject
    protected PubSubService pubSubService;
    @Inject
    protected SysConfigService sysConfigService;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    public void init() {
        pubSubService.reg(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, this);
    }

    @Override
    public void onMessage(String channel, String message) {
        log.debug("WebPubSub onMessage::" + message);
        switch (message) {
            case "sys_param":
                Globals.initSysParam(sysConfigService);
                break;
            case "sys_wx":
                Globals.initWx();
                break;
            default:
                break;
        }
    }
}
