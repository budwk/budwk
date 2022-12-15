package com.budwk.starter.websocket;

import com.budwk.starter.websocket.hanlder.WebSocketHandler;
import com.budwk.starter.websocket.publish.RedisMessagePubSub;
import com.budwk.starter.websocket.room.JedisRoomProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author wizzer.cn
 */
@ServerEndpoint(value = "/websocket", configurator = WkWsConfigurator.class)
@IocBean(create = "init")
@Slf4j
public class WkWebSocket extends AbstractWsEndpoint {
    @Inject
    protected PropertiesProxy conf;
    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        roomProvider = ioc.get(JedisRoomProvider.class);
        ioc.get(RedisMessagePubSub.class).setRoomProvider(roomProvider).setSession(sessions);
    }

    @Override
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return ioc.get(WebSocketHandler.class);
    }

}
