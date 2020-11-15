package com.budwk.nb.web.commons.ext.websocket;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;
import redis.clients.jedis.*;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_WSROOM;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@ServerEndpoint(value = "/websocket", configurator = NutWsConfigurator.class)
@IocBean(create = "init")
public class WkWebSocket extends AbstractWsEndpoint implements PubSub {
    protected static final Log log = Logs.get();
    @Inject
    protected PubSubService pubSubService;
    @Inject
    protected JedisAgent jedisAgent;
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject("java:$conf.getInt('shiro.session.cache.redis.ttl')")
    private int REDIS_KEY_SESSION_TTL;

    @Override
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return ioc.get(WkWsHandler.class);
    }

    public void init() {
        roomProvider = new WkJedisRoomProvider(jedisAgent, REDIS_KEY_SESSION_TTL);
        if (jedisAgent.isClusterMode()) {
            JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
            List<String> keys = new ArrayList<>();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + "*");
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keys.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                }
            }
            try (Jedis jedis = jedisAgent.getResource()) {
                for (String key : keys) {
                    switch (jedis.type(key)) {
                        case "none":
                            break;
                        case "set":
                            break;
                        default:
                            jedis.del(key);
                    }
                }
            }
        } else {
            try (Jedis jedis = jedisAgent.getResource()) {
                ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + "*");
                ScanResult<String> scan = null;
                do {
                    scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                    for (String key : scan.getResult()) {
                        switch (jedis.type(key)) {
                            case "none":
                                break;
                            case "set":
                                break;
                            default:
                                jedis.del(key);
                        }
                    }
                } while (!scan.isCompleteIteration());
            }
        }
        pubSubService.reg(REDIS_KEY_WSROOM + "*", this);
    }


    @Override
    public void onMessage(String channel, String message) {
        log.debugf("GET PubSub channel=%s msg=%s", channel, message);
        each(channel, (index, session, length) -> session.getAsyncRemote().sendText(message));
    }
}
