package com.budwk.starter.websocket.publish;

import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;
import java.util.Set;

/**
 * @author wizzer.cn
 */
@IocBean
public class RedisMessageServiceImpl implements MessageService {

    @Inject
    private RedisService redisService;
    @Inject
    private PubSubService pubSubService;

    @Override
    public void wsSendMsg(String userId, String token, String msg) {
        String tokenString = RedisConstant.WS_TOKEN + userId + ":" + token;
        pubSubService.fire(tokenString, msg);
    }

    @Override
    public void wsSendMsg(String userId, String msg) {
        String matchString = RedisConstant.WS_ROOM + userId;
        Set<String> tokens = redisService.smembers(matchString);
        for (String key : tokens) {
            pubSubService.fire(key, msg);
        }
    }

    @Override
    public void wsSendMsg(List<String> userIdList, String msg) {
        for (String userId : userIdList) {
            this.wsSendMsg(userId, msg);
        }
    }
}
