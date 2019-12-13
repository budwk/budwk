package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_app_list;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

public interface SysAppListService extends BaseService<Sys_app_list> {
    List<String> getAppNameList();
}
