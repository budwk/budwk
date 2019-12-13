package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_user;
import com.budwk.nb.wx.services.WxUserService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxUserService.class)
public class WxUserServiceImpl extends BaseServiceImpl<Wx_user> implements WxUserService {
    public WxUserServiceImpl(Dao dao) {
        super(dao);
    }
}
