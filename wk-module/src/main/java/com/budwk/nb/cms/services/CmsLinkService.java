package com.budwk.nb.cms.services;

import com.budwk.nb.cms.models.Cms_link;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

public interface CmsLinkService extends BaseService<Cms_link>{
    List<Cms_link> getLinkList(String code, int size);
    void clearCache();
}
