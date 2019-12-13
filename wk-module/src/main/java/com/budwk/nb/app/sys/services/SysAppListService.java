package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_app_list;
import com.budwk.nb.common.base.service.BaseService;

import java.util.List;

public interface SysAppListService extends BaseService<Sys_app_list> {
    List<String> getAppNameList();
}
