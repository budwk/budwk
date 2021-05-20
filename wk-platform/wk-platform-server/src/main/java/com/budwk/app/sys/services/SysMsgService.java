package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_msg;
import com.budwk.app.sys.models.Sys_msg_user;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysMsgService extends BaseService<Sys_msg> {
    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return
     */
    int getUnreadNum(String userId);

    /**
     * 获取未读消息列表
     *
     * @param userId     用户ID
     * @param pageNumber 页码
     * @param pageSize   页大小
     * @return
     */
    List<Sys_msg_user> getUnreadList(String userId, int pageNumber, int pageSize);

    /**
     * 撤销消息
     * @param id
     */
    void deleteMsg(String id);

    /**
     * 发送消息
     * @param msg 消息体
     * @param userId 用户ID
     */
    void saveMsg(Sys_msg msg,String[] userId);

}
