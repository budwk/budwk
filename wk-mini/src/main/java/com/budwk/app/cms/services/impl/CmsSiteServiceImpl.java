package com.budwk.app.cms.services.impl;


import com.budwk.app.cms.models.Cms_site;
import com.budwk.app.cms.services.CmsSiteService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;


@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "cms_site", isHash = true, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class CmsSiteServiceImpl extends BaseServiceImpl<Cms_site> implements CmsSiteService {
    public CmsSiteServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    @CacheResult
    public Cms_site getSite(String code) {
        return this.fetch(Cnd.where("id", "=", code));
    }

    @Override
    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
