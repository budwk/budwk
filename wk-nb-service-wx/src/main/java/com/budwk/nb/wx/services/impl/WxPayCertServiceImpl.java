package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_pay_cert;
import com.budwk.nb.wx.services.WxPayCertService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxPayCertService.class)
public class WxPayCertServiceImpl extends BaseServiceImpl<Wx_pay_cert> implements WxPayCertService {
    public WxPayCertServiceImpl(Dao dao) {
        super(dao);
    }

}
