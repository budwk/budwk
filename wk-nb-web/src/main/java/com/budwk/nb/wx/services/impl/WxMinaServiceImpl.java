package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_mina;
import com.budwk.nb.wx.services.WxMinaService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class WxMinaServiceImpl extends BaseServiceImpl<Wx_mina> implements WxMinaService {
    public WxMinaServiceImpl(Dao dao) {
        super(dao);
    }
}