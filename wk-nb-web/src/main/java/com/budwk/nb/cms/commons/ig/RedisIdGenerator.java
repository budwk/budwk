package com.budwk.nb.cms.commons.ig;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2018/3/17.
 */

@IocBean
public class RedisIdGenerator implements IdGenerator {

    @Inject
    protected JedisAgent jedisAgent;
    private final static int ID_LENGTH = 16;

    public RedisIdGenerator() {
    }

    public RedisIdGenerator(JedisAgent jedisAgent) {
        this.jedisAgent = jedisAgent;
    }

    @Override
    public String next(String tableName, String prefix) {
        String key = prefix.toUpperCase();
        if (key.length() > ID_LENGTH) {
            key = key.substring(0, ID_LENGTH);
        }
        try (Jedis jedis = jedisAgent.getResource()) {
            String ym = Times.format("yyyyMM", new Date());
            String id = String.valueOf(jedis.incr("budwk:ig:cms:" + tableName.toUpperCase() + ym));
            return key + ym + Strings.alignRight(id, 10, '0');
        }
    }

    @Override
    public Object run(List<Object> fetchParam) {
        return next((String) fetchParam.get(0), (String) fetchParam.get(1));
    }

    @Override
    public String fetchSelf() {
        return "ig";
    }

}