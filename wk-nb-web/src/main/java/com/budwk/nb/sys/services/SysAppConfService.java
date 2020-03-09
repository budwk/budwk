package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_app_conf;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
public interface SysAppConfService extends BaseService<Sys_app_conf> {
    /**
     * 获取配置列表
     * @return
     */
    List<String> getConfNameList();
}
