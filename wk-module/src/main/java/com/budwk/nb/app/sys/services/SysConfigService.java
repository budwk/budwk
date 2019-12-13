package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_config;
import com.budwk.nb.common.base.service.BaseService;

import java.util.List;

/**
 * Created by wizzer on 2016/12/23.
 */
public interface SysConfigService extends BaseService<Sys_config> {
    /**
     * 查询所有数据
     * @return
     */
    List<Sys_config> getAllList();
}
