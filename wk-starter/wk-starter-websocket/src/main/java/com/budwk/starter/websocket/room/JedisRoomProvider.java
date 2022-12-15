package com.budwk.starter.websocket.room;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.websocket.WsRoomProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
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
    protected JedisAgent jedisAgent;
    @Inject("java:$conf.getInt('websocket.timeout',3600)")
    protected int sessionTimeout;

    @Override
    public Set<String> wsids(String room) {
        try (Jedis jedis = jedisAgent.getResource()) {
            return jedis.smembers(room);
        }
    }

    @Override
    public void join(String userId, String token, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            // room 里存 token
            jedis.sadd(RedisConstant.WS_ROOM + userId, RedisConstant.WS_TOKEN + userId + ":" + token);
            // token 里存 session
            jedis.sadd(RedisConstant.WS_TOKEN + userId + ":" + token, wsid);
            jedis.expire(RedisConstant.WS_ROOM + userId, sessionTimeout);
            jedis.expire(RedisConstant.WS_TOKEN + userId + ":" + token, sessionTimeout);
        }
    }

    @Override
    public void left(String userId, String token, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            jedis.srem(RedisConstant.WS_ROOM + userId, token);
            jedis.srem(RedisConstant.WS_TOKEN + userId + ":" + token, wsid);
        }
    }
}
