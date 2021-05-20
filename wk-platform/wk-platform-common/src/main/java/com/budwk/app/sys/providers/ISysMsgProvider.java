package com.budwk.app.sys.providers;

import com.budwk.app.sys.enums.SysMsgType;

import java.util.List;

/**
 * 站内消息服务
 *
 * @author wizzer@qq.com
 */
public interface ISysMsgProvider {

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
