package com.budwk.app.cms.services;


import com.budwk.app.cms.models.Cms_link;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
public interface CmsLinkService extends BaseService<Cms_link> {
    /**
     * 获取链接列表
     * @param code 标识符
     * @param size 大小
     * @return
     */
    List<Cms_link> getLinkList(String code, int size);

    /**
     * 清空缓存
     */
    void cacheClear();
}
