package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_reply_news;
import com.budwk.nb.wx.services.WxReplyNewsService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxReplyNewsService.class)
public class WxReplyNewsServiceImpl extends BaseServiceImpl<Wx_reply_news> implements WxReplyNewsService {
    public WxReplyNewsServiceImpl(Dao dao) {
        super(dao);
    }
}
