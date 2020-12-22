package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_msg_reply;
import com.budwk.nb.wx.services.WxMsgReplyService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class WxMsgReplyServiceImpl extends BaseServiceImpl<Wx_msg_reply> implements WxMsgReplyService {
    public WxMsgReplyServiceImpl(Dao dao) {
        super(dao);
    }
}
