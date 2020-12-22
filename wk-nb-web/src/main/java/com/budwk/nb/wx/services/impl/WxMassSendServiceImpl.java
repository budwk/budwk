package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_mass_send;
import com.budwk.nb.wx.services.WxMassSendService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class WxMassSendServiceImpl extends BaseServiceImpl<Wx_mass_send> implements WxMassSendService {
    public WxMassSendServiceImpl(Dao dao) {
        super(dao);
    }
}
