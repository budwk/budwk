package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_app_conf;
import com.budwk.nb.common.base.service.BaseService;

import java.util.List;

public interface SysAppConfService extends BaseService<Sys_app_conf> {
    List<String> getConfNameList();
}
