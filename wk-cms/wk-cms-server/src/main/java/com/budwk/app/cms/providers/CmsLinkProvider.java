package com.budwk.app.cms.providers;

import org.apache.dubbo.config.annotation.DubboService;
import com.budwk.app.cms.models.Cms_link;
import com.budwk.app.cms.services.CmsLinkService;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author caoshi
 */
@DubboService(interfaceClass = ICmsLinkProvider.class)
@IocBean
public class CmsLinkProvider implements ICmsLinkProvider{
    @Inject
    private CmsLinkService cmsLinkService;


    @Override
    public List<Cms_link> getLinkList(String code, int size) {
        return cmsLinkService.getLinkList(code,size);
    }

    @Override
    public Cms_link getLink(Condition cnd) {
        return cmsLinkService.fetch(cnd);
    }

    @Override
    public Pagination listPage(int page, int pageSize, Condition cnd) {
        return cmsLinkService.listPage(page,pageSize,cnd);
    }
}
