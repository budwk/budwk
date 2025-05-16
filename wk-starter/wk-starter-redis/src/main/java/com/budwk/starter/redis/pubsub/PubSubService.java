package com.budwk.starter.redis.pubsub;

import com.budwk.starter.redis.RedisStarter;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@IocBean(depose = "depose")
public class PubSubService {
    private static final Log log = Logs.get();

    @Inject
    protected RedisStarter redisStarter;

    protected List<PubSubProxy> list = new ArrayList<PubSubProxy>();
    protected Set<String> patterns = new HashSet<String>();

    public void reg(final String pattern, PubSub pb) {
        final PubSubProxy proxy = new PubSubProxy(pattern, pb);
        list.add(proxy);
        Thread t = new Thread("jedis.pubsub." + pattern) {
            public void run() {
                while (patterns.contains(pattern)) {
                    try {
                        redisStarter.getUnifiedJedis().psubscribe(proxy, pattern);
                    } catch (Exception e) {
                        if (!patterns.contains(pattern))
                            break;
                        log.warn("something wrong!! sleep 3s", e);
                        try {
                            Thread.sleep(3000);
                        } catch (Throwable _e) {
                            break;
                        }
                    }
                }
            }
        };
        t.start();
        patterns.add(pattern);
    }

    public void fire(String channel, String message) {
        log.debugf("publish channel=%s msg=%s", channel, message);
        redisStarter.getUnifiedJedis().publish(channel, message);
    }

    public void depose() {
        for (PubSubProxy proxy : list)
            try {
                patterns.remove(proxy.pattern);
                proxy.punsubscribe(proxy.pattern);
            } catch (Exception e) {
                log.debug("punsubscribe " + proxy.pattern, e);
            }
    }

}
