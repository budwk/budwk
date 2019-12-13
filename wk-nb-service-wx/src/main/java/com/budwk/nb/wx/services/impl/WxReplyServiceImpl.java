package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_reply;
import com.budwk.nb.wx.services.WxReplyService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxReplyService.class)
public class WxReplyServiceImpl extends BaseServiceImpl<Wx_reply> implements WxReplyService {
    public WxReplyServiceImpl(Dao dao) {
        super(dao);
    }
}
