package com.budwk.app.cms.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.cms.models.Cms_article;
import com.budwk.app.cms.services.CmsArticleService;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author caoshi
 * @date 2021年5月10日
 */
@Service(interfaceClass = ICmsArticleProvider.class)
@IocBean
public class CmsArticleProvider implements ICmsArticleProvider{

    @Inject
    private CmsArticleService cmsArticleService;


    @Override
    public Pagination listPage(int page, int pageSize, Condition cnd) {
        return cmsArticleService.listPage(page,pageSize,cnd);
    }

    @Override
    public Cms_article getArticle(Condition cnd) {
        return cmsArticleService.getArticle(cnd);
    }

    @Override
    public List<Cms_article> list(Condition cnd) {
        return cmsArticleService.query(cnd);
    }

}
