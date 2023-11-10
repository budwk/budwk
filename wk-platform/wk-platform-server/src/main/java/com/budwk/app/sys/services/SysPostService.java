package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_post;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysPostService extends BaseService<Sys_post> {
    void importData(String fileName, List<Sys_post> list, boolean over, String userId, String loginname);
}
