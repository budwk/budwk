package com.budwk.nb.wx.services.impl;

import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_tpl_id;
import com.budwk.nb.wx.services.WxTplIdService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class WxTplIdServiceImpl extends BaseServiceImpl<Wx_tpl_id> implements WxTplIdService {
    public WxTplIdServiceImpl(Dao dao) {
        super(dao);
    }
}
