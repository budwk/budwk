package com.budwk.starter.rediscache;

import com.budwk.starter.redis.RedisService;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public abstract class AbstractWkcacheInterceptor implements MethodInterceptor {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    private PropertiesProxy conf;

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    protected RedisService getRedisService() {
        return ioc.get(RedisService.class);
    }

    protected PropertiesProxy getConf() {
        if (conf == null)
            conf = ioc.get(PropertiesProxy.class, "conf");
        return conf;
    }
}
