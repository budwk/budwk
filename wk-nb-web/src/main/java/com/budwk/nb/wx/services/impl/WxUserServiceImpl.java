package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_user;
import com.budwk.nb.wx.services.WxUserService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class WxUserServiceImpl extends BaseServiceImpl<Wx_user> implements WxUserService {
    public WxUserServiceImpl(Dao dao) {
        super(dao);
    }
}
