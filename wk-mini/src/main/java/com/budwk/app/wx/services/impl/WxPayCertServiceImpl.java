package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_pay_cert;
import com.budwk.app.wx.services.WxPayCertService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxPayCertServiceImpl extends BaseServiceImpl<Wx_pay_cert> implements WxPayCertService {
    public WxPayCertServiceImpl(Dao dao) {
        super(dao);
    }
}