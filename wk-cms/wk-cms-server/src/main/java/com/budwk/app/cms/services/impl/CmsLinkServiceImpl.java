package com.budwk.app.cms.services.impl;

import com.budwk.app.cms.models.Cms_link;
import com.budwk.app.cms.models.Cms_link_class;
import com.budwk.app.cms.services.CmsLinkClassService;
import com.budwk.app.cms.services.CmsLinkService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.ArrayList;
import java.util.List;


@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "cms_link", isHash = true, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class CmsLinkServiceImpl extends BaseServiceImpl<Cms_link> implements CmsLinkService {
    public CmsLinkServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private CmsLinkClassService cmsLinkClassService;

    @Override
    @CacheResult
    public List<Cms_link> getLinkList(String code, int size) {
        List<Cms_link> links = new ArrayList<>();
        Cms_link_class cmsLinkClass = cmsLinkClassService.fetch(Cnd.where("code", "=", code));
        if (cmsLinkClass != null) {
            Pager pager = new Pager();
            pager.setPageSize(size);
            pager.setPageNumber(1);
            links = this.query(Cnd.where("classId", "=", cmsLinkClass.getId()).desc("createdAt"), pager);
        }
        return links;
    }

    @Override
    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
