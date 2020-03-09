package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.commons.base.service.BaseService;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
public interface SysMsgService extends BaseService<Sys_msg> {

    /**
     * 保存信息同时发送
     * @param sysMsg 消息对象
     * @param users  用户数组
     * @return
     */
    Sys_msg saveMsg(Sys_msg sysMsg, String[] users);

    /**
     * 删除消息及消息用户表数据
     *
     * @param id 消息ID
     */
    void deleteMsg(String id);
}
