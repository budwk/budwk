package com.budwk.nb.cms.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.app.cms.models.Cms_article;
import com.budwk.nb.app.cms.services.CmsArticleService;
import com.budwk.nb.common.base.service.BaseServiceImpl;
import com.budwk.nb.common.base.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=CmsArticleService.class)
@CacheDefaults(cacheName = "cms_article")
public class CmsArticleServiceImpl extends BaseServiceImpl<Cms_article> implements CmsArticleService {
    public CmsArticleServiceImpl(Dao dao) {
        super(dao);
    }

    @CacheResult
    public Pagination getListPage(int pageNumber, int pageSize, Condition cnd) {
        return this.listPage(pageNumber, pageSize, cnd);
    }

    @CacheResult
    public Cms_article getArticle(Condition cnd) {
        return this.fetch(cnd);
    }

    @CacheRemoveAll
    public void clearCache() {

    }
}
