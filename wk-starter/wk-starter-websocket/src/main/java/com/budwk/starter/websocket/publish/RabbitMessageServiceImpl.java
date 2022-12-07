package com.budwk.starter.websocket.publish;

import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean
public class RabbitMessageServiceImpl implements MessageService {


    @Override
    public void wsSendMsg(String userId, String token, String msg) {
        String matchString = RedisConstant.WS_ROOM + userId + ":" + token;

    }

    @Override
    public void wsSendMsg(String userId, String msg) {
        String matchString = RedisConstant.WS_ROOM + userId + ":*";

    }

    @Override
    public void wsSendMsg(List<String> userId, String msg) {
        String matchString = RedisConstant.WS_ROOM + "*";

    }
}
