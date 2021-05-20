package com.budwk.app.cms.services.impl;

import com.budwk.app.cms.models.Cms_article;
import com.budwk.app.cms.services.CmsArticleService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;


/**
 * @author wizzer(wizzer@qq.com) on 2018/3/16.
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "cms_article", isHash = true, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class CmsArticleServiceImpl extends BaseServiceImpl<Cms_article> implements CmsArticleService {
    public CmsArticleServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    @CacheResult
    public Pagination getListPage(int pageNumber, int pageSize, Condition cnd) {
        return this.listPage(pageNumber, pageSize, cnd);
    }

    @Override
    @CacheResult
    public Cms_article getArticle(Condition cnd) {
        return this.fetch(cnd);
    }

    @Override
    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
