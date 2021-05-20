package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysAppService extends BaseService<Sys_app> {

    List<Sys_app> listAll();

    List<Sys_app> listEnable();

    void cacheClear();
}
