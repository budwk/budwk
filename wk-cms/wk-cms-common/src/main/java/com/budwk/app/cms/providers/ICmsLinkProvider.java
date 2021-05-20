package com.budwk.app.cms.providers;

import com.budwk.app.cms.models.Cms_link;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;

import java.util.List;

/**
 * @author caoshi
 */
public interface ICmsLinkProvider {

    List<Cms_link> getLinkList(String code, int size);

    Cms_link getLink(Condition cnd);

    Pagination listPage(int page,int pageSize,Condition cnd);
}
