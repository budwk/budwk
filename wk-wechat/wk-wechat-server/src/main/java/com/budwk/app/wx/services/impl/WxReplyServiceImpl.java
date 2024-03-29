package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_reply;
import com.budwk.app.wx.services.WxReplyService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxReplyServiceImpl extends BaseServiceImpl<Wx_reply> implements WxReplyService {
    public WxReplyServiceImpl(Dao dao) {
        super(dao);
    }
}