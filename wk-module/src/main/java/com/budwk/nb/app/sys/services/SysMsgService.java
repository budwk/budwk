package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_msg;
import com.budwk.nb.common.base.service.BaseService;

public interface SysMsgService extends BaseService<Sys_msg> {
    /**
     * 保存信息同时发送
     *
     * @param sysMsg
     * @param users
     */
    Sys_msg saveMsg(Sys_msg sysMsg, String[] users);

    /**
     * 删除消息及消息用户表数据
     *
     * @param id
     */
    void deleteMsg(String id);
}
