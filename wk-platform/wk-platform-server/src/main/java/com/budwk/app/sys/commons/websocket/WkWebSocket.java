package com.budwk.app.sys.commons.websocket;

import com.budwk.starter.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;
import redis.clients.jedis.*;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@ServerEndpoint(value = "/websocket", configurator = NutWsConfigurator.class)
@IocBean(create = "init")
@Slf4j
public class WkWebSocket extends AbstractWsEndpoint implements PubSub {
    @Inject
    protected PubSubService pubSubService;

    @Inject
    protected JedisAgent jedisAgent;
    @Inject
    protected RedisService redisService;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    // 取token失效时间
    @Inject("java:$conf.getInt('web.wsroom.timeout',3600)")
    private int tokenTimeout;

    @Override
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return ioc.get(WkWsHandler.class);
    }

    public void init() {
        roomProvider = new WkJedisRoomProvider(jedisAgent, tokenTimeout);
        String matchString = RedisConstant.WS_ROOM + "*";
        List<String> keys = new ArrayList<>();
        if (jedisAgent.isClusterMode()) {
            JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanParams match = new ScanParams().match(matchString);
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keys.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                }
            }
        } else {
            try (Jedis jedis = jedisAgent.getResource()) {
                ScanParams match = new ScanParams().match(matchString);
                ScanResult<String> scan = null;
                do {
                    scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                    keys.addAll(scan.getResult());
                } while (!scan.isCompleteIteration());
            }
        }
        for (String key : keys) {
            switch (redisService.type(key)) {
                case "none":
                    break;
                case "set":
                    break;
                default:
                    redisService.del(key);
            }
        }
        pubSubService.reg(RedisConstant.WS_ROOM + "*", this);
    }

    @Override
    public void onMessage(String channel, String message) {
        log.debug("WkWebSocket GET PubSub channel={} msg={}", channel, message);
        each(channel, (index, session, length) -> session.getAsyncRemote().sendText(message));
    }
}
