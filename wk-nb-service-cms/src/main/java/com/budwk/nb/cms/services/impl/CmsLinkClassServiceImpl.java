package com.budwk.nb.cms.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.app.cms.models.Cms_link_class;
import com.budwk.nb.app.cms.services.CmsLinkClassService;
import com.budwk.nb.common.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=CmsLinkClassService.class)
public class CmsLinkClassServiceImpl extends BaseServiceImpl<Cms_link_class> implements CmsLinkClassService {
    public CmsLinkClassServiceImpl(Dao dao) {
        super(dao);
    }
}
