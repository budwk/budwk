package com.budwk.nb.sys.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_app_list;
import com.budwk.nb.sys.services.SysAppListService;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/2/27.
 */
@IocBean(args = {"refer:dao"})
public class SysAppListServiceImpl extends BaseServiceImpl<Sys_app_list> implements SysAppListService {
    public SysAppListServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public List<String> getAppNameList() {
        Sql sql = Sqls.create("SELECT DISTINCT appName FROM sys_app_list");
        sql.setCallback(Sqls.callback.strs());
        this.dao().execute(sql);
        return sql.getList(String.class);
    }
}