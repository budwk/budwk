package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_msg;
import com.budwk.nb.wx.services.WxMsgService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxMsgService.class)
public class WxMsgServiceImpl extends BaseServiceImpl<Wx_msg> implements WxMsgService {
    public WxMsgServiceImpl(Dao dao) {
        super(dao);
    }
}
