package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_msg;
import com.budwk.nb.wx.services.WxMsgService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class WxMsgServiceImpl extends BaseServiceImpl<Wx_msg> implements WxMsgService {
    public WxMsgServiceImpl(Dao dao) {
        super(dao);
    }
}
