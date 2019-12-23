package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.commons.base.service.BaseService;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
public interface SysMsgService extends BaseService<Sys_msg> {

    /**
     * 保存信息同时发送
     * @param sysMsg
     * @param users
     * @return
     */
    Sys_msg saveMsg(Sys_msg sysMsg, String[] users);

    /**
     * 删除消息及消息用户表数据
     *
     * @param id
     */
    void deleteMsg(String id);
}
