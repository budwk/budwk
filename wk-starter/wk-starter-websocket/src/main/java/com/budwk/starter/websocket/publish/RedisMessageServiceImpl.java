package com.budwk.starter.websocket.publish;

import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wizzer.cn
 */
@IocBean
public class RedisMessageServiceImpl implements MessageService {

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
            Set<String> keys = redisService.keys(matchString);
            if (keys != null && keys.size() > 0) {
                keyList.addAll(keys);
            }
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
}
