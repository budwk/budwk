package com.budwk.app.sys.services;

import com.budwk.app.sys.enums.SysMsgType;
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
     *
     * @param id
     */
    void deleteMsg(String id);

    /**
     * 发送消息
     *
     * @param msg    消息体
     * @param userId 用户ID
     */
    void saveMsg(Sys_msg msg, String[] userId);

    /**
     * 向指定token发送通知消息(不入库,仅通过ws通知)
     *
     * @param userId 用户ID
     * @param token  token值
     * @param msg    消息内容
     */
    void wsSendMsg(String userId, String token, String msg);

    /**
     * 发送通知消息(不入库,仅通过ws通知)
     *
     * @param userId 用户ID
     * @param msg    消息内容
     */
    void wsSendMsg(String userId, String msg);

    /**
     * 发送通知消息(不入库,仅通过ws通知)
     *
     * @param userId 用户ID
     * @param msg    消息内容
     */
    void wsSendMsg(List<String> userId, String msg);

    /**
     * 检查用户登录
     *
     * @param userId
     * @param token
     */
    void wsCheckLogin(String userId, String token);

    /**
     * 发送消息
     *
     * @param userId     用户ID
     * @param type       消息类型
     * @param title      消息标题
     * @param url        URL
     * @param note       消息内容
     * @param sendUserId 发送人
     */
    void sendMsg(String userId, SysMsgType type, String title, String url, String note, String sendUserId);

    /**
     * 发送消息
     *
     * @param userId     用户ID数组
     * @param type       消息类型
     * @param title      消息标题
     * @param url        URL
     * @param note       消息内容
     * @param sendUserId 发送人
     */
    void sendMsg(String[] userId, SysMsgType type, String title, String url, String note, String sendUserId);

    /**
     * 获取用户登录系统后的消息通知
     *
     * @param userId 用户ID
     * @param notify 是否弹出通知框
     */
    void getMsg(String userId, boolean notify);

}
