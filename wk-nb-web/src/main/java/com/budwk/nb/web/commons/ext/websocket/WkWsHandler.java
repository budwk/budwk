package com.budwk.nb.web.commons.ext.websocket;

import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.sys.services.SysMsgService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_WSROOM;

@IocBean
public class WkWsHandler extends SimpleWsHandler {
    private static final Log log = Logs.get();
    @Inject
    private SysMsgService sysMsgService;
    @Inject
    private RedisService redisService;

    /**
     * 加入房间 对应的消息是  {action:"join", room:"wendal"}
     */
    @Override
    public void join(NutMap req) {
        String sessionId = redisService.get(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + req.getString("uid") + ":" + req.getString("token"));
        // 增加用户是否登陆的判断,防止越权获取信息
        if (Strings.isNotBlank(sessionId)) {
            join(req.getString("room") + "," + req.getString("token"));
        }
    }

    /**
     * 退出房间 对应的消息是 {action:"left", room:"wendal"}
     */
    @Override
    public void left(NutMap req) {
        left(req.getString("room") + "," + req.getString("token"));
    }

    @Override
    public void join(String room) {
        if (!Strings.isBlank(room)) {
            String[] tmp = StringUtils.split(room, ",");
            room = REDIS_KEY_WSROOM + tmp[0] + ":" + tmp[1];
            log.debugf("session(id=%s) join room(name=%s)", session.getId(), room);
            roomProvider.join(room, session.getId());
            sysMsgService.getMsg(tmp[0]);
        }
    }

    @Override
    public void left(String room) {
        if (!Strings.isBlank(room)) {
            String[] tmp = StringUtils.split(room, ",");
            room = REDIS_KEY_WSROOM + tmp[0] + ":" + tmp[1];
            log.debugf("session(id=%s) left room(name=%s)", session.getId(), room);
            roomProvider.left(room, session.getId());
            redisService.del(room);
        }
    }

    @Override
    public void depose() {

    }
}
