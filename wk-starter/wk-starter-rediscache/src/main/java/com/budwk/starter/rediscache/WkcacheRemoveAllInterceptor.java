package com.budwk.starter.rediscache;

import com.budwk.starter.rediscache.annotation.CacheDefaults;
import com.budwk.starter.rediscache.annotation.CacheRemoveAll;
import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheRemoveAllInterceptor extends AbstractWkcacheInterceptor {
    private String cacheName;
    private boolean isHash;

    public void prepare(CacheDefaults cacheDefaults, CacheRemoveAll cacheRemoveAll, Method method) {
        cacheName = Strings.sNull(cacheRemoveAll.cacheName());
        isHash = cacheDefaults != null && cacheDefaults.isHash();
        if (Strings.isBlank(cacheName)) {
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
    }

    public void filter(InterceptorChain chain) throws Throwable {
        if (cacheName.contains(",")) {
            for (String name : cacheName.split(",")) {
                if (isHash) {
                    delHashCache(name);
                } else {
                    delCache(name);
                }
            }
        } else {
            if (isHash) {
                delHashCache(cacheName);
            } else {
                delCache(cacheName);
            }
        }
        chain.doChain();
    }

    private void delCache(String cacheName) {
        String lua = "local keysToDelete = redis.call('KEYS', ARGV[1])\n" +
                "for _, key in ipairs(keysToDelete) do\n" +
                "    redis.call('DEL', key)\n" +
                "end";

        if (getRedisService().isCluster()) {
            JedisCluster jedisCluster = (JedisCluster) getRedisService().getClient();
            Map<String, ConnectionPool> nodes = jedisCluster.getClusterNodes();
            for (ConnectionPool pool : nodes.values()) {
                try (Jedis jedis = new Jedis(pool.getResource())) {
                    boolean isMaster = Strings.sNull(jedis.info("replication")).contains("role:master");
                    if(isMaster) {
                        jedis.eval(lua, 0, cacheName + ":*");
                    }
                }
            }
        } else {
            getRedisService().getClient().eval(lua, 0, cacheName + ":*");
        }
    }

    private void delHashCache(String cacheName) {
        getRedisService().del(cacheName.getBytes());
    }
}
