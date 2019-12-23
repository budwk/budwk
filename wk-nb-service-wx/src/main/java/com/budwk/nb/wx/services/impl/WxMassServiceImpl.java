package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_mass;
import com.budwk.nb.wx.services.WxMassService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer@qq.com) on 2019/3/16.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxMassService.class)
public class WxMassServiceImpl extends BaseServiceImpl<Wx_mass> implements WxMassService {
    public WxMassServiceImpl(Dao dao) {
        super(dao);
    }
}
