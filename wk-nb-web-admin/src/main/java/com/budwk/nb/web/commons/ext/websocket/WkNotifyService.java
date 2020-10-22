package com.budwk.nb.web.commons.ext.websocket;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.sys.enums.SysMsgType;
import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.sys.models.Sys_msg_user;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysMsgUserService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_WSROOM;

/**
 * @author wizzer(wizzer @ qq.com) on 2018/6/28.
 */
@IocBean
public class WkNotifyService {
    private static final Log log = Logs.get();
    @Inject
    private PubSubService pubSubService;
    @Inject
    @Reference
    private SysMsgService sysMsgService;
    @Inject
    @Reference
    private SysMsgUserService sysMsgUserService;
    @Inject
    private RedisService redisService;

    @Async
    public void notify(Sys_msg innerMsg, String[] rooms) {
        NutMap map = new NutMap();
        map.put("action", "notify");
        map.put("title", innerMsg.getTitle());
        map.put("id", innerMsg.getId());
        map.put("url", innerMsg.getUrl());
        map.put("type", innerMsg.getType().getValue());
        String msg = Json.toJson(map, JsonFormat.compact());
        // 系统消息发送给所有在线用户
        if (SysMsgType.SYSTEM.equals(innerMsg.getType())) {
            ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + "*");
            ScanResult<String> scan = null;
            do {
                scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                for (String room : scan.getResult()) {
                    pubSubService.fire(room, msg);
                    getMsg(room.split(":")[2]);
                }
            } while (!scan.isCompleteIteration());
        } else if (SysMsgType.USER.equals(innerMsg.getType())) {
            for (String room : rooms) {
                getMsg(room);
                ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + room + ":*");
                ScanResult<String> scan = null;
                do {
                    scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                    for (String key : scan.getResult()) {
                        pubSubService.fire(key, msg);
                    }
                } while (!scan.isCompleteIteration());
            }
        }
    }

    @Async
    public void innerMsg(String room, int size, List<NutMap> list) {
        NutMap map = new NutMap();
        map.put("action", "notice");
        // 未读消息数
        map.put("size", size);
        // 最新3条消息列表  type--系统消息/用户消息  title--标题  time--时间戳
        map.put("list", list);
        String msg = Json.toJson(map, JsonFormat.compact());
        log.debugf("room=%s,msg=%s", room, msg);
        ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + room + ":*");
        ScanResult<String> scan = null;
        do {
            scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
            for (String key : scan.getResult()) {
                pubSubService.fire(key, msg);
            }
        } while (!scan.isCompleteIteration());
    }


    @Async
    public void getMsg(String loginname) {
        try {
            //通过用户名查询未读消息
            int size = sysMsgUserService.getUnreadNum(loginname);
            List<Sys_msg_user> list = sysMsgUserService.getUnreadList(loginname, 1, 5);
            List<NutMap> mapList = new ArrayList<>();
            for (Sys_msg_user msgUser : list) {
                if (Lang.isNotEmpty(msgUser.getMsg())) {
                    mapList.add(NutMap.NEW().addv("msgId", msgUser.getMsgId()).addv("type", msgUser.getMsg().getType().getValue())
                            .addv("typeText", msgUser.getMsg().getType().getText())
                            .addv("title", msgUser.getMsg().getTitle())
                            .addv("url", msgUser.getMsg().getUrl())
                            .addv("time", Times.format("yyyy-MM-dd HH:mm", Times.D(msgUser.getMsg().getSendAt()))));
                }
            }
            innerMsg(loginname, size, mapList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void offline(String room, String userToken) {
        NutMap map = new NutMap();
        map.put("action", "offline");
        String msg = Json.toJson(map, JsonFormat.compact());
        try {
            room = REDIS_KEY_WSROOM + room + ":" + userToken;
            log.debugf("offline room(name=%s)", room);
            pubSubService.fire(room, msg);
            redisService.expire(room, 60 * 3);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
