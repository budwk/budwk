package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_msg_user;
import com.budwk.nb.base.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
public interface SysMsgUserService extends BaseService<Sys_msg_user> {

    /**
     * 获取未读消息数量
     *
     * @param loginname 用户名
     * @return
     */
    int getUnreadNum(String loginname);

    /**
     * 获取未读消息列表
     *
     * @param loginname 用户名
     * @param pageNumber 页码
     * @param pageSize 页大小
     * @return
     */
    List<Sys_msg_user> getUnreadList(String loginname, int pageNumber, int pageSize);

    /**
     * 删除用户缓存
     *
     * @param loginname 用户名
     */
    void deleteCache(String loginname);

    /**
     * 清空缓存
     */
    void clearCache();
}
