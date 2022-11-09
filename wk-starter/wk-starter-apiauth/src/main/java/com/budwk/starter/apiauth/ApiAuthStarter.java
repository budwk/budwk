package com.budwk.starter.apiauth;

import com.budwk.starter.apiauth.storage.CacheStorage;
import com.budwk.starter.apiauth.storage.MemoryCacheStorage;
import com.budwk.starter.apiauth.storage.RedisCacheStorage;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ApiAuthStarter {

    @Inject
    private PropertiesProxy conf;

    @Inject
    private Ioc ioc;

    @IocBean
    public CacheStorage getCacheStorage() {
        if (classExist("org.nutz.integration.jedis.RedisService")) {
            return new RedisCacheStorage(ioc.get(RedisService.class));
        }
        return new MemoryCacheStorage();
    }

    private boolean classExist(String className) {
        try {
            Class.forName(className, false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
