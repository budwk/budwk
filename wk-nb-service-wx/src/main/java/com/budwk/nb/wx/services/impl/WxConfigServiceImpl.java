package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_config;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxConfigService.class)
public class WxConfigServiceImpl extends BaseServiceImpl<Wx_config> implements WxConfigService {
    public WxConfigServiceImpl(Dao dao) {
        super(dao);
    }

}
