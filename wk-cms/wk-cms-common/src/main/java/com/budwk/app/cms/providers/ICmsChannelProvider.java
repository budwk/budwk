package com.budwk.app.cms.providers;

import com.budwk.app.cms.models.Cms_channel;
import com.budwk.starter.common.page.Pagination;
import org.nutz.dao.Condition;

import java.util.List;

/**
 * @author caoshi
 */
public interface ICmsChannelProvider {

    List<Cms_channel> list(Condition cnd);

    List<Cms_channel> listChannel(String parentId,String parentCode);

    Cms_channel getChannel(String id,String code);

    Pagination listPage(int page,int pageSize,Condition cnd);
}
