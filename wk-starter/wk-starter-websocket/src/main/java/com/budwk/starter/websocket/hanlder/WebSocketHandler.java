package com.budwk.starter.websocket.hanlder;

import com.budwk.starter.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import javax.websocket.MessageHandler;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class WebSocketHandler extends AbstractWsHandler implements MessageHandler.Whole<String> {

    public void join(String userId, String token) {
        String room = RedisConstant.WS_ROOM + userId + ":" + token;
        log.debug("session(id={}) join room(name={})", session.getId(), room);
        this.rooms.add(room);
        this.roomProvider.join(userId, token, session.getId());
    }

    public void left(String userId, String token) {
        String room = RedisConstant.WS_ROOM + userId + ":" + token;
        log.debug("session(id={}) left room(name={})", session.getId(), room);
        this.rooms.remove(room);
        this.roomProvider.left(userId, token, session.getId());
    }

    /**
     * 加入房间 对应的消息是  {action:"join", userId:"", token: ""}
     */
    public void join(NutMap req) {
        join(req.getString("userId"), req.getString("token"));
    }

    /**
     * 退出房间 对应的消息是 {action:"left", userId:"", token: ""}
     */
    public void left(NutMap req) {
        left(req.getString("userId"), req.getString("token"));
    }

    @Override
    public void depose() {
        for (Object str : rooms.toArray()) {
            String room = String.valueOf(str);
            int index = room.lastIndexOf(":");
            left(room.substring(0, index), room.substring(index + 1));
        }
    }
}
