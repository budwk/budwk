package com.budwk.app.cms.services;

import com.budwk.app.cms.models.Cms_article;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseService;
import org.nutz.dao.Condition;

public interface CmsArticleService extends BaseService<Cms_article> {
    /**
     * 获取文章列表
     *
     * @param pageNumber 页码
     * @param pageSize 页大小
     * @param cnd 查询条件
     * @return
     */
    Pagination getListPage(int pageNumber, int pageSize, Condition cnd);

    /**
     * 从缓存根据条件查询一篇文章
     *
     * @param cnd
     * @return
     */
    Cms_article getArticle(Condition cnd);

    /**
     * 清空缓存
     */
    void cacheClear();
}
