package com.budwk.app.cms.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.cms.models.Cms_site;
import com.budwk.app.cms.services.CmsSiteService;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author caoshi
 */
@Service(interfaceClass = ICmsSiteProvider.class)
@IocBean
public class CmsSiteProvider implements ICmsSiteProvider{
    @Inject
    private CmsSiteService cmsSiteService;


    @Override
    public Cms_site getSite(String code) {
        return cmsSiteService.getSite(code);
    }

    @Override
    public List<Cms_site> list(Condition cnd) {
        return cmsSiteService.query(cnd);
    }

    @Override
    public Pagination listPage(int page, int pageSize, Condition cnd) {
        return cmsSiteService.listPage(page,pageSize,cnd);
    }
}
