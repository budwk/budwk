package com.budwk.nb.wx.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.wx.models.Wx_tpl_log;
import com.budwk.nb.wx.services.WxTplLogService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class WxTplLogServiceImpl extends BaseServiceImpl<Wx_tpl_log> implements WxTplLogService {
    public WxTplLogServiceImpl(Dao dao) {
        super(dao);
    }
}
