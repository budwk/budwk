package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_mina;
import com.budwk.nb.wx.services.WxMinaService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxMinaService.class)
public class WxMinaServiceImpl extends BaseServiceImpl<Wx_mina> implements WxMinaService {
    public WxMinaServiceImpl(Dao dao) {
        super(dao);
    }
}