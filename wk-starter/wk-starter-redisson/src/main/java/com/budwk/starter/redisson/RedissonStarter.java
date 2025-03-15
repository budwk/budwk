package com.budwk.starter.redisson;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

@IocBean
public class RedissonStarter {
    @Inject
    protected PropertiesProxy conf;

    @IocBean(name = "redissonConfig")
    public Config createredissonConfig() {
        String host = conf.get("redis.host", "127.0.0.1");
        int port = conf.getInt("redis.port", 6379);
        String user = conf.get("redis.user", null);
        String password = conf.get("redis.password", null);
        int database = conf.getInt("redis.database", 0);
        int maxTotal = conf.getInt("redis.maxTotal", 8);
        int maxIdle = conf.getInt("redis.maxIdle", 8);
        String mode = conf.get("redis.mode");
        Config config = new Config();
        if ("normal".equalsIgnoreCase(mode)) {
            SingleServerConfig ssc = config.useSingleServer();
            if (Strings.isNotBlank(user)) {
                ssc.setUsername(user);
            }
            if (Strings.isNotBlank(password)) {
                ssc.setPassword(password);
            }
            ssc.setDatabase(database);
            ssc.setConnectionPoolSize(maxTotal);
            ssc.setAddress("redis://" + host + ":" + port);
        } else if ("cluster".equalsIgnoreCase(mode)) {
            ClusterServersConfig csc = config.useClusterServers();
            String nodes = conf.get("redis.nodes", "");
            if (Strings.isBlank(nodes)) {
                throw new NullPointerException("cluster redis nodes configuration didn't find");
            }
            String[] hosts = nodes.split(",");
            for (String h : hosts) {
                csc.addNodeAddress("redis://" + h);
            }
            if (Strings.isNotBlank(user)) {
                csc.setUsername(user);
            }
            if (Strings.isNotBlank(password)) {
                csc.setPassword(password);
            }
            csc.setSubscriptionConnectionPoolSize(maxTotal);
        }
        config.setCodec(new FstCodec());
        return config;
    }

    @IocBean(name = "redissonClient")
    public RedissonClient createRedissonClient(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.create(redissonConfig);
    }

    @IocBean(name = "redissonRxClient")
    public RedissonRxClient createRedissonRxClient(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.createRx(redissonConfig);
    }

    @IocBean(name = "redissonReactiveClient")
    public RedissonReactiveClient createRedissonReactive(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.createReactive(redissonConfig);
    }
}
