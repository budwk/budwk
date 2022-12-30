package com.budwk.starter.websocket.publish;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
public class MessageSendServer {
    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    private MessageService messageService;

    public void init() {
        if ("rabbitmq".equalsIgnoreCase(conf.get("websocket.type"))) {
            messageService = ioc.get(RabbitMessageServiceImpl.class);
        } else {
            messageService = ioc.get(RedisMessageServiceImpl.class);
        }
    }

    /**
     * 向用户单个session实例发送消息
     *
     * @param userId
     * @param token
     * @param msg
     */
    public void wsSendMsg(String userId, String token, String msg) {
        messageService.wsSendMsg(userId, token, msg);
    }

    /**
     * 向用户所有session实例发送消息
     *
     * @param userId
     * @param msg
     */
    public void wsSendMsg(String userId, String msg) {
        messageService.wsSendMsg(userId, msg);
    }

    /**
     * 向所有用户发送消息
     *
     * @param userIdList
     * @param msg
     */
    public void wsSendMsg(List<String> userIdList, String msg) {
        messageService.wsSendMsg(userIdList, msg);
    }
}
