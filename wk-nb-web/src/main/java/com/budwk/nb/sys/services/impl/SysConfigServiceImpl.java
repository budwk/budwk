package com.budwk.nb.sys.services.impl;

import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_config;
import com.budwk.nb.sys.services.SysConfigService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2016/12/23.
 */
@IocBean(args = {"refer:dao"})
public class SysConfigServiceImpl extends BaseServiceImpl<Sys_config> implements SysConfigService {
    public SysConfigServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public List<Sys_config> getAllList() {
        return this.query(Cnd.where("delFlag", "=", false));
    }
}