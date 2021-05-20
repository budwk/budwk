package com.budwk.app.cms.providers;


import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.cms.models.Cms_channel;
import com.budwk.app.cms.services.CmsChannelService;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author caoshi
 */
@Service(interfaceClass = ICmsChannelProvider.class)
@IocBean
public class CmsChannelProvider implements ICmsChannelProvider{

    @Inject
    private CmsChannelService cmsChannelService;

    @Override
    public List<Cms_channel> list(Condition cnd) {
        return cmsChannelService.query(cnd);
    }

    @Override
    public List<Cms_channel> listChannel(String parentId, String parentCode) {
        return cmsChannelService.listChannel(parentId,parentCode);
    }

    @Override
    public Cms_channel getChannel(String id, String code) {
        return cmsChannelService.getChannel(id,code);
    }

    @Override
    public Pagination listPage(int page, int pageSize, Condition cnd) {
        return cmsChannelService.listPage(page,pageSize,cnd);
    }
}
