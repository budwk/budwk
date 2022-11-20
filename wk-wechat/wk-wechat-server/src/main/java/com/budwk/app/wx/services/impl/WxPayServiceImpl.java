package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_pay;
import com.budwk.app.wx.services.WxPayService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxPayServiceImpl extends BaseServiceImpl<Wx_pay> implements WxPayService {
    public WxPayServiceImpl(Dao dao) {
        super(dao);
    }
}