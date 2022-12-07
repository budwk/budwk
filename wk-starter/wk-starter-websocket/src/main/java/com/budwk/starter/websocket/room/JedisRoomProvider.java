package com.budwk.starter.websocket.room;

import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;
import redis.clients.jedis.Jedis;

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
    public void join(String room, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            jedis.sadd(room, wsid);
            jedis.expire(room, sessionTimeout);
        }
    }

    @Override
    public void left(String room, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            jedis.srem(room, wsid);
        }
    }
}
