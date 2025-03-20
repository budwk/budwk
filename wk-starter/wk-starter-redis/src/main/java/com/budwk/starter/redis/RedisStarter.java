package com.budwk.starter.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.ClusterConnectionProvider;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@IocBean(depose = "depose")
public class RedisStarter {
    @Inject
    private PropertiesProxy conf;
    private UnifiedJedis client = null;

    @IocBean
    public UnifiedJedis unifiedJedis() {
        String host = conf.get("redis.host", "127.0.0.1");
        int port = conf.getInt("redis.port", 6379);
        String user = conf.get("redis.user", null);
        String password = conf.get("redis.password", null);
        int database = conf.getInt("redis.database", 0);
        int maxTotal = conf.getInt("redis.maxTotal", 8);
        int maxIdle = conf.getInt("redis.maxIdle", 8);
        String mode = conf.get("redis.mode");

        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder()
                .user(user)
                .password(password)
                .database(database).build();

        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        if ("normal".equalsIgnoreCase(mode)) {
            HostAndPort config = new HostAndPort(host, port);
            ConnectionFactory factory = new ConnectionFactory(config, jedisClientConfig);
            PooledConnectionProvider provider = new PooledConnectionProvider(factory, poolConfig);
            client = new UnifiedJedis(provider);
        } else if ("cluster".equalsIgnoreCase(mode)) {
            String nodes = conf.get("redis.nodes", "");
            if (Strings.isBlank(nodes)) {
                throw new NullPointerException("cluster redis nodes configuration didn't find");
            }
            String[] hosts = nodes.split(",");
            Set<HostAndPort> config = new HashSet<>();
            for (String address : hosts) {
                config.add(HostAndPort.from(address));
            }
            client = new JedisCluster(config, jedisClientConfig, poolConfig);
        }
        return client;
    }

    public void depose() {
        if (client != null) {
            client.close();
        }
    }

}
