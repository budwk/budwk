package com.budwk.nb.wx.services.impl;

import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_tpl_list;
import com.budwk.nb.wx.services.WxTplListService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/3/16.
 */
@IocBean(args = {"refer:dao"})
public class WxTplListServiceImpl extends BaseServiceImpl<Wx_tpl_list> implements WxTplListService {
    public WxTplListServiceImpl(Dao dao) {
        super(dao);
    }
}
