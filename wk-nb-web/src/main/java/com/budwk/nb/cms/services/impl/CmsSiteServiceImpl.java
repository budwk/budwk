package com.budwk.nb.cms.services.impl;

import com.budwk.nb.cms.models.Cms_site;
import com.budwk.nb.cms.services.CmsSiteService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "cms_site")
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
    public void clearCache() {

    }
}
