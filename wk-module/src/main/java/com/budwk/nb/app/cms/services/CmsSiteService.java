package com.budwk.nb.app.cms.services;

import com.budwk.nb.app.cms.models.Cms_site;
import com.budwk.nb.common.base.service.BaseService;

public interface CmsSiteService extends BaseService<Cms_site> {
    /**
     * 通过编码获取站点信息
     *
     * @param code
     * @return
     */
    Cms_site getSite(String code);

    /**
     * 清空缓存
     */
    void clearCache();
}
