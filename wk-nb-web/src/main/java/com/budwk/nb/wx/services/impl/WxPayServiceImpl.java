package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_pay;
import com.budwk.nb.wx.services.WxPayService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class WxPayServiceImpl extends BaseServiceImpl<Wx_pay> implements WxPayService {
    public WxPayServiceImpl(Dao dao) {
        super(dao);
    }

}
