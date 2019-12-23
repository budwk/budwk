package com.budwk.nb.wx.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.wx.models.Wx_tpl_log;
import com.budwk.nb.wx.services.WxTplLogService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass=WxTplLogService.class)
public class WxTplLogServiceImpl extends BaseServiceImpl<Wx_tpl_log> implements WxTplLogService {
    public WxTplLogServiceImpl(Dao dao) {
        super(dao);
    }
}
