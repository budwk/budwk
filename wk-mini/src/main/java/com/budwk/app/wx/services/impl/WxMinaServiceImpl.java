package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_mina;
import com.budwk.app.wx.services.WxMinaService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxMinaServiceImpl extends BaseServiceImpl<Wx_mina> implements WxMinaService {
    public WxMinaServiceImpl(Dao dao) {
        super(dao);
    }
}