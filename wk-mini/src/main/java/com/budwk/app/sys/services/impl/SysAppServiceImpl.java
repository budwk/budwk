package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;


/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_app", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class SysAppServiceImpl extends BaseServiceImpl<Sys_app> implements SysAppService {
    public SysAppServiceImpl(Dao dao) {
        super(dao);
    }

    @CacheResult(cacheKey = "listAll")
    public List<Sys_app> listAll() {
        return this.query("^(id|name|path|disabled)$", Cnd.NEW().asc("location"));
    }

    @CacheResult(cacheKey = "listEnable")
    public List<Sys_app> listEnable() {
        return this.query("^(id|name|path|disabled)$", Cnd.where("disabled", "=", false).asc("location"));
    }

    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
