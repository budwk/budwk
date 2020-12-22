package com.budwk.nb.sys.services.impl;

import com.budwk.nb.base.page.Pagination;
import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.sys.enums.SysMsgType;
import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.sys.models.Sys_msg_user;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysMsgUserService;
import com.budwk.nb.sys.services.SysUserService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.jedis.JedisAgent;
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
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;

import static com.budwk.nb.base.constant.RedisConstant.REDIS_KEY_WSROOM;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
public class SysMsgServiceImpl extends BaseServiceImpl<Sys_msg> implements SysMsgService {
    public SysMsgServiceImpl(Dao dao) {
        super(dao);
    }

    private static final Log log = Logs.get();
    @Inject
    private SysMsgUserService sysMsgUserService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private JedisAgent jedisAgent;
    @Inject
    private PubSubService pubSubService;
    @Inject
    private RedisService redisService;

    /**
     * 发送消息,已考虑用户比较多的情况,采用分页发送
     *
     * @param sysMsg
     * @param users
     * @return
     */
    @Override
    public Sys_msg saveMsg(Sys_msg sysMsg, String[] users) {
        Sys_msg dbMsg = this.insert(sysMsg);
        if (dbMsg != null) {
            if (SysMsgType.USER.equals(dbMsg.getType()) && users != null) {
                for (String loginname : users) {
                    Sys_msg_user sys_msg_user = new Sys_msg_user();
                    sys_msg_user.setMsgId(dbMsg.getId());
                    sys_msg_user.setStatus(0);
                    sys_msg_user.setLoginname(loginname);
                    sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                    sys_msg_user.setUpdatedBy(dbMsg.getUpdatedBy());
                    sysMsgUserService.insert(sys_msg_user);
                }
            }
            if (SysMsgType.SYSTEM.equals(dbMsg.getType())) {
                Cnd cnd = Cnd.where("disabled", "=", false).and("delFlag", "=", false);
                int total = sysUserService.count(cnd);
                int size = 1000;
                Pagination pagination = new Pagination();
                pagination.setTotalCount(total);
                pagination.setPageSize(size);
                for (int i = 1; i <= pagination.getTotalPage(); i++) {
                    Pagination pagination2 = sysUserService.listPage(i, size, cnd);
                    for (Object sysUser : pagination2.getList()) {
                        Sys_user user = (Sys_user) sysUser;
                        Sys_msg_user sys_msg_user = new Sys_msg_user();
                        sys_msg_user.setMsgId(dbMsg.getId());
                        sys_msg_user.setStatus(0);
                        sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                        sys_msg_user.setUpdatedBy(dbMsg.getUpdatedBy());
                        sys_msg_user.setLoginname(user.getLoginname());
                        sysMsgUserService.insert(sys_msg_user);
                    }
                }
            }
        }
        sysMsgUserService.clearCache();
        notify(dbMsg, users);
        return dbMsg;
    }

    @Override
    public void deleteMsg(String id) {
        this.vDelete(id);
        sysMsgUserService.vDelete(Cnd.where("msgId", "=", id));
        sysMsgUserService.clearCache();
    }

    @Override
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
            if (jedisAgent.isClusterMode()) {

                JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
                for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                    try (Jedis jedis = pool.getResource()) {
                        ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + "*");
                        ScanResult<String> scan = null;
                        do {
                            scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                            for (String room : scan.getResult()) {
                                pubSubService.fire(room, msg);
                                getMsg(room.split(":")[2]);
                            }
                        } while (!scan.isCompleteIteration());
                    }
                }
            } else {
                ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + "*");
                ScanResult<String> scan = null;
                do {
                    scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                    for (String room : scan.getResult()) {
                        pubSubService.fire(room, msg);
                        getMsg(room.split(":")[2]);
                    }
                } while (!scan.isCompleteIteration());
            }
        } else if (SysMsgType.USER.equals(innerMsg.getType())) {
            for (String room : rooms) {
                getMsg(room);
                if (jedisAgent.isClusterMode()) {
                    JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
                    for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                        try (Jedis jedis = pool.getResource()) {
                            ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + room + ":*");
                            ScanResult<String> scan = null;
                            do {
                                scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                                for (String key : scan.getResult()) {
                                    pubSubService.fire(key, msg);
                                }
                            } while (!scan.isCompleteIteration());
                        }
                    }
                } else {
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
    }

    @Override
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
        if (jedisAgent.isClusterMode()) {
            JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanParams match = new ScanParams().match(REDIS_KEY_WSROOM + room + ":*");
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        for (String key : scan.getResult()) {
                            pubSubService.fire(key, msg);
                        }
                    } while (!scan.isCompleteIteration());
                }
            }
        } else {
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

    @Override
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

    @Override
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
