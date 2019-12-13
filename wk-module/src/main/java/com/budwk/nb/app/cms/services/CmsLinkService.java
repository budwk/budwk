package com.budwk.nb.app.cms.services;

import com.budwk.nb.app.cms.models.Cms_link;
import com.budwk.nb.common.base.service.BaseService;

import java.util.List;

public interface CmsLinkService extends BaseService<Cms_link>{
    List<Cms_link> getLinkList(String code, int size);
    void clearCache();
}
