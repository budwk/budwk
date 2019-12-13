package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_app_conf;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

public interface SysAppConfService extends BaseService<Sys_app_conf> {
    List<String> getConfNameList();
}
