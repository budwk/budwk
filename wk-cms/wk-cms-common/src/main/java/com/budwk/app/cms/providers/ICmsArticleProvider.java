package com.budwk.app.cms.providers;

import com.budwk.app.cms.models.Cms_article;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;

import java.util.List;

/**
 * @author caoshi
 * @date 2021年5月10日
 */
public interface ICmsArticleProvider {

    Pagination listPage(int page, int pageSize, Condition cnd);

    Cms_article getArticle(Condition cnd);

    List<Cms_article> list(Condition cnd);
}
