package com.budwk.starter.websocket.hanlder;

import cn.dev33.satoken.stp.StpUtil;
import com.budwk.starter.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class WebSocketHandler extends SimpleWsHandler {
    @Override
    public void join(NutMap req) {
        String userId = req.getString("userId", "");
        String token = req.getString("token", "");
        // 判断token是否存在,做简单的权限验证
        if (StpUtil.getTokenValueListByLoginId(userId).contains(token)) {
            this.join(userId, token);
        }
    }

    public void join(String userId, String token) {
        String room = RedisConstant.WS_ROOM + userId + ":" + token;
        log.debug("session(id={}) join room(name={})", session.getId(), room);
        this.rooms.add(room);
        this.roomProvider.join(room, session.getId());
    }

    @Override
    public void left(NutMap req) {
        String userId = req.getString("userId", "");
        String token = req.getString("token", "");
        this.left(userId, token);
    }

    public void left(String userId, String token) {
        String room = RedisConstant.WS_ROOM + userId + ":" + token;
        log.debug("session(id={}) left room(name={})", session.getId(), room);
        this.rooms.remove(room);
        this.roomProvider.left(room, session.getId());
    }
}
