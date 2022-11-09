package com.budwk.app.sys.providers;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.enums.SysMsgType;
import com.budwk.app.sys.models.Sys_msg;
import com.budwk.app.sys.models.Sys_msg_user;
import com.budwk.app.sys.services.SysMsgService;
import com.budwk.app.sys.services.SysMsgUserService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.BaseException;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = ISysMsgProvider.class)
@IocBean
public class SysMsgProvider implements ISysMsgProvider {
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysMsgService sysMsgService;
    @Inject
    private SysMsgUserService sysMsgUserService;
    @Inject
    private JedisAgent jedisAgent;
    @Inject
    private RedisService redisService;
    @Inject
    private PubSubService pubSubService;

    public List<String> getRedisKeys(String matchString) {
        List<String> keyList = new ArrayList<>();
        if (jedisAgent.isClusterMode()) {
            JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanParams match = new ScanParams().match(matchString);
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keyList.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                }
            }
        } else {
            ScanParams match = new ScanParams().match(matchString);
            ScanResult<String> scan = null;
            do {
                scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                keyList.addAll(scan.getResult());
            } while (!scan.isCompleteIteration());
        }
        return keyList;
    }

    @Override
    public void wsSendMsg(String userId, String token, String msg) {
        String matchString = RedisConstant.WS_ROOM + userId + ":" + token;
        List<String> keyList = getRedisKeys(matchString);
        for (String key : keyList) {
            pubSubService.fire(key, msg);
        }
    }

    @Override
    public void wsSendMsg(String userId, String msg) {
        String matchString = RedisConstant.WS_ROOM + userId + ":*";
        List<String> keyList = getRedisKeys(matchString);
        for (String key : keyList) {
            pubSubService.fire(key, msg);
        }
    }

    @Override
    public void wsSendMsg(List<String> userId, String msg) {
        String matchString = RedisConstant.WS_ROOM + "*";
        List<String> keyList = getRedisKeys(matchString);
        for (String key : keyList) {
            String[] k = Strings.splitIgnoreBlank(key, ":");
            if (userId.contains(k[2])) {
                pubSubService.fire(key, msg);
            }
        }
    }

    @Override
    public void wsCheckLogin(String userId, String token) {
        if (!StpUtil.getTokenValueListByLoginId(userId).contains(token)) {
            throw new BaseException("WS 用户登录信息不正确");
        }
    }

    @Override
    public void sendMsg(String userId, SysMsgType type, String title, String url, String note, String sendUserId) {
        this.sendMsg(new String[]{userId}, type, title, url, note, sendUserId);
    }

    @Override
    public void sendMsg(String[] userId, SysMsgType type, String title, String url, String note, String sendUserId) {
        Sys_msg msg = new Sys_msg();
        msg.setType(type);
        msg.setScope(SysMsgScope.SCOPE);
        msg.setNote(note);
        msg.setTitle(title);
        msg.setUrl(url);
        msg.setSendAt(System.currentTimeMillis());
        msg.setCreatedBy(Strings.sNull(sendUserId));
        sysMsgService.saveMsg(msg, userId);
    }

    @Override
    @Async
    public void getMsg(String userId, boolean notify) {
        int size = sysMsgService.getUnreadNum(userId);
        List<Sys_msg_user> list = sysMsgService.getUnreadList(userId, 1, 5);
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
        NutMap map = new NutMap();
        map.put("action", "notice");
        // 未读消息数
        map.put("size", size);
        map.put("notify", notify);
        // 最新3条消息列表  type--系统消息/用户消息  title--标题  time--时间戳
        map.put("list", mapList);
        String msg = Json.toJson(map, JsonFormat.compact());
        this.wsSendMsg(userId, msg);
    }
}
