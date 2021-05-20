package com.budwk.app.cms.providers;

import com.budwk.app.cms.models.Cms_site;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;

import java.util.List;

public interface ICmsSiteProvider {

    Cms_site getSite(String code);

    List<Cms_site> list(Condition cnd);

    Pagination listPage(int page,int pageSize,Condition cnd);
}
