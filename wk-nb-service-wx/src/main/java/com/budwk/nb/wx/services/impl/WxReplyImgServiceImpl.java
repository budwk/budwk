package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_reply_img;
import com.budwk.nb.wx.services.WxReplyImgService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass = WxReplyImgService.class)
public class WxReplyImgServiceImpl extends BaseServiceImpl<Wx_reply_img> implements WxReplyImgService {
    public WxReplyImgServiceImpl(Dao dao) {
        super(dao);
    }
}
