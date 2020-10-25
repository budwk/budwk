package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.commons.base.service.BaseService;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
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


    /**
     * 通知客户端弹窗
     * @param innerMsg
     * @param rooms
     */
    void notify(Sys_msg innerMsg, String rooms[]);

    /**
     * 通知客户端有新消息及消息数量
     * @param room
     * @param size
     * @param list
     */
    void innerMsg(String room, int size, List<NutMap> list);

    /**
     * 获取某用户的未读消息数量及列表
     * @param loginname
     */
    void getMsg(String loginname);

    /**
     * 通知下线
     * @param loginname
     * @param httpSessionId
     */
    void offline(String loginname, String httpSessionId);
}
