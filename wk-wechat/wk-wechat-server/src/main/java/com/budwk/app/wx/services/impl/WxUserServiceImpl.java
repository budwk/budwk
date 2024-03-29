package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_user;
import com.budwk.app.wx.services.WxUserService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxUserServiceImpl extends BaseServiceImpl<Wx_user> implements WxUserService {
    public WxUserServiceImpl(Dao dao) {
        super(dao);
    }
}