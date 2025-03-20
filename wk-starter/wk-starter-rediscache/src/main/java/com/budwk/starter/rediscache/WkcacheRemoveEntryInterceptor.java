package com.budwk.starter.rediscache;

import com.budwk.starter.rediscache.annotation.CacheDefaults;
import com.budwk.starter.rediscache.annotation.CacheRemove;
import org.nutz.aop.InterceptorChain;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheRemoveEntryInterceptor extends AbstractWkcacheInterceptor {
    private String cacheKeyTemp;
    private String cacheName;
    private boolean isHash;
    private List<String> paramNames;

    public void prepare(CacheDefaults cacheDefaults, CacheRemove cacheRemove, Method method) {
        cacheKeyTemp = Strings.sNull(cacheRemove.cacheKey());
        cacheName = Strings.sNull(cacheRemove.cacheName());
        isHash = cacheDefaults != null && cacheDefaults.isHash();
        if (Strings.isBlank(cacheName)) {
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        paramNames = MethodParamNamesScaner.getParamNames(method);
    }

    public void filter(InterceptorChain chain) throws Throwable {
        String cacheKey = cacheKeyTemp;
        Method method = chain.getCallingMethod();
        if (Strings.isBlank(cacheKey)) {
            cacheKey = method.getDeclaringClass().getName()
                    + "."
                    + method.getName()
                    + "#"
                    + Arrays.toString(chain.getArgs());
        } else {
            CharSegment key = new CharSegment(cacheKey);
            if (key.hasKey()) {
                Context ctx = Lang.context();
                Object[] args = chain.getArgs();
                if (paramNames != null) {
                    for (int i = 0; i < paramNames.size() && i < args.length; i++) {
                        ctx.set(paramNames.get(i), args[i]);
                    }
                }
                ctx.set("args", args);
                Context _ctx = Lang.context();
                for (String val : key.keys()) {
                    _ctx.set(val, new El(val).eval(ctx));
                }
                cacheKey = key.render(_ctx).toString();
            } else {
                cacheKey = key.getOrginalString();
            }
        }
        if (cacheKey.contains(",")) {
            for (String key : cacheKey.split(",")) {
                delCache(cacheName, key);
            }
        } else {
            delCache(cacheName, cacheKey);
        }
        chain.doChain();
    }

    private void delCache(String cacheName, String cacheKey) {
        if (cacheKey.endsWith("*")) {
            if (isHash) {
                String lua = "local hashKey = KEYS[1]\n" +
                        "local prefix = ARGV[1]\n" +
                        "local fields = redis.call('HKEYS', hashKey)\n" +
                        "for _, field in ipairs(fields) do\n" +
                        "    if string.sub(field, 1, string.len(prefix)) == prefix then\n" +
                        "        redis.call('HDEL', hashKey, field)\n" +
                        "    end\n" +
                        "end";
                if (getRedisService().isCluster()) {
                    JedisCluster jedisCluster = (JedisCluster) getRedisService().getClient();
                    Map<String, ConnectionPool> nodes = jedisCluster.getClusterNodes();
                    for (ConnectionPool pool : nodes.values()) {
                        try (Jedis jedis = new Jedis(pool.getResource())) {
                            boolean isMaster = Strings.sNull(jedis.info("replication")).contains("role:master");
                            if(isMaster) {
                                jedis.eval(lua, Collections.singletonList(cacheName), Collections.singletonList(cacheKey.substring(0, cacheKey.lastIndexOf("*"))));
                            }
                        }
                    }
                } else {
                    getRedisService().getClient().eval(lua, Collections.singletonList(cacheName), Collections.singletonList(cacheKey.substring(0, cacheKey.lastIndexOf("*"))));

                }
            } else {
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
                                jedis.eval(lua, 0, cacheName + ":" + cacheKey);
                            }
                        }
                    }
                } else {
                    getRedisService().getClient().eval(lua, 0, cacheName + ":" + cacheKey);
                }
            }
        } else {
            if (isHash) {
                getRedisService().hdel(cacheName.getBytes(), cacheKey.getBytes());
            } else {
                getRedisService().del((cacheName + ":" + cacheKey).getBytes());
            }
        }
    }

}
