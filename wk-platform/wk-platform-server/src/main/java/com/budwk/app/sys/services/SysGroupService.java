package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_group;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer@qq.com
 */
public interface SysGroupService extends BaseService<Sys_group> {
    /**
     * 删除角色组
     * @param groupId
     */
    void clearGroup(String groupId);
}
