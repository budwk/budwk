package com.budwk.nb.cms.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.cms.models.Cms_article;
import com.budwk.nb.cms.services.CmsArticleService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.commons.base.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

/**
 * @author wizzer(wizzer@qq.com) on 2018/3/16.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass=CmsArticleService.class)
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX +"cms_article")
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
    public void clearCache() {

    }
}
