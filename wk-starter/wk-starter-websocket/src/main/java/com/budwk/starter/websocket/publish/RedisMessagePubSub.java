package com.budwk.starter.websocket.publish;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.websocket.WsRoomProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.*;

import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
@Slf4j
public class RedisMessagePubSub implements PubSub {
    @Inject
    protected PubSubService pubSubService;
    protected ConcurrentHashMap<String, Session> sessions;
    protected WsRoomProvider roomProvider;

    public void init() {
        pubSubService.reg(RedisConstant.WS_TOKEN + "*", this);
    }

    public RedisMessagePubSub setRoomProvider(WsRoomProvider roomProvider) {
        this.roomProvider = roomProvider;
        return this;
    }

    public RedisMessagePubSub setSession(ConcurrentHashMap<String, Session> sessions) {
        this.sessions = sessions;
        return this;
    }

    @Override
    public void onMessage(String room, String message) {
        log.debug("WkWebSocket GET PubSub room={} msg={}", room, message);
        Set<String> wsids = roomProvider.wsids(room);
        log.debug(room);
        log.debug(Json.toJson(wsids));
        if (wsids != null && !wsids.isEmpty()) {
            String[] tmp = wsids.toArray(new String[wsids.size()]);
            Lang.each(tmp, new Each<String>() {
                public void invoke(int index, String ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                    Session session = getSession(ele);
                    if (session != null) {
                        session.getAsyncRemote().sendText(message);
                    }
                }
            });
        }
    }

    public Session getSession(String room) {
        return this.getSession(room, true);
    }

    public Session getSession(String room, boolean opened) {
        Session session = (Session) this.sessions.get(room);
        if (session == null) {
            return null;
        } else {
            return opened && !session.isOpen() ? null : session;
        }
    }
}
