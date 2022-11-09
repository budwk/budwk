package com.budwk.app.sys.commons.websocket;

import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author wizzer@qq.com
 */
@Slf4j
public class WkJedisRoomProvider implements WsRoomProvider {

    protected JedisAgent jedisAgent;
    protected int sessionTimeout;

    public WkJedisRoomProvider(JedisAgent jedisAgent, int sessionTimeout) {
        this.jedisAgent = jedisAgent;
        this.sessionTimeout = sessionTimeout;
    }

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
