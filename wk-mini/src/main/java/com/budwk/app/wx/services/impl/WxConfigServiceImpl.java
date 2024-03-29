package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxConfigServiceImpl extends BaseServiceImpl<Wx_config> implements WxConfigService {
    public WxConfigServiceImpl(Dao dao) {
        super(dao);
    }
}