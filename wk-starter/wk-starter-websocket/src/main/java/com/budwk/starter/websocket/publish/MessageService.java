package com.budwk.starter.websocket.publish;

import java.util.List;

/**
 * @author wizzer.cn
 */
public interface MessageService {
    /**
     * 向用户单个session实例发送消息
     * @param userId
     * @param token
     * @param msg
     */
    void wsSendMsg(String userId, String token, String msg);

    /**
     * 向用户所有session实例发送消息
     * @param userId
     * @param msg
     */
    void wsSendMsg(String userId, String msg);

    /**
     * 向所有用户发送消息
     * @param userId
     * @param msg
     */
    void wsSendMsg(List<String> userId, String msg);
}
