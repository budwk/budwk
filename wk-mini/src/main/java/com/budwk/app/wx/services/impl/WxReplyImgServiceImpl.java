package com.budwk.app.wx.services.impl;

import com.budwk.app.wx.models.Wx_reply_img;
import com.budwk.app.wx.services.WxReplyImgService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class WxReplyImgServiceImpl extends BaseServiceImpl<Wx_reply_img> implements WxReplyImgService {
    public WxReplyImgServiceImpl(Dao dao) {
        super(dao);
    }
}