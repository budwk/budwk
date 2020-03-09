package com.budwk.nb.cms.services;

import com.budwk.nb.cms.models.Cms_link;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
public interface CmsLinkService extends BaseService<Cms_link>{
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
    void clearCache();
}
