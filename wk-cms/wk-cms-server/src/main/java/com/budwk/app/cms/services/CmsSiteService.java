package com.budwk.app.cms.services;


import com.budwk.app.cms.models.Cms_site;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer(wizzer@qq.com) on 2018/3/16.
 */
public interface CmsSiteService extends BaseService<Cms_site> {
    /**
     * 通过编码获取站点信息
     *
     * @param code 站点标识
     * @return
     */
    Cms_site getSite(String code);

    /**
     * 清空缓存
     */
    void cacheClear();
}
