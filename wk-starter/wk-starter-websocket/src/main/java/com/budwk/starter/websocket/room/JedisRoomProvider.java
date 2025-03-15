package com.budwk.starter.websocket.room;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.redis.RedisService;
import com.budwk.starter.websocket.WsRoomProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Set;

/**
 * @author wizzer@qq.com
 */
@IocBean
@Slf4j
public class JedisRoomProvider implements WsRoomProvider {
    @Inject
    protected PropertiesProxy conf;
    @Inject
    protected RedisService redisService;
    @Inject("java:$conf.getInt('websocket.timeout',3600)")
    protected int sessionTimeout;

    @Override
    public Set<String> wsids(String room) {
        return redisService.smembers(room);
    }

    @Override
    public void join(String userId, String token, String wsid) {
        // room 里存 token
        redisService.sadd(RedisConstant.WS_ROOM + userId, RedisConstant.WS_TOKEN + userId + ":" + token);
        // token 里存 session
        redisService.sadd(RedisConstant.WS_TOKEN + userId + ":" + token, wsid);
        redisService.expire(RedisConstant.WS_ROOM + userId, sessionTimeout);
        redisService.expire(RedisConstant.WS_TOKEN + userId + ":" + token, sessionTimeout);

    }

    @Override
    public void left(String userId, String token, String wsid) {
        redisService.srem(RedisConstant.WS_ROOM + userId, token);
        redisService.srem(RedisConstant.WS_TOKEN + userId + ":" + token, wsid);
    }
}
