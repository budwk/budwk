package com.budwk.starter.redisjson;

import com.budwk.jedis.*;
import com.budwk.jedis.providers.PooledConnectionProvider;
import com.budwk.jedis.providers.ShardedConnectionProvider;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@IocBean
public class RedisJsonStarter {
    @Inject
    private PropertiesProxy conf;

    @IocBean(name = "reJsonClient")
    public UnifiedJedis reJsonClient() {
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

        UnifiedJedis client = null;
        if (mode.equals("normal")) {
            HostAndPort config = new HostAndPort(host, port);
            ConnectionFactory factory = new ConnectionFactory(config, jedisClientConfig);
            PooledConnectionProvider provider = new PooledConnectionProvider(factory, poolConfig);
            client = new UnifiedJedis(provider);
        } else if (mode.equals("cluster")) {
            String nodes = conf.get("redis.nodes", "");
            if (Strings.isBlank(nodes)) {
                throw new NullPointerException("cluster redis nodes configuration didn't find");
            }
            String[] hosts = nodes.split(",");
            Set<HostAndPort> config = new HashSet<>();
            for (String address : hosts) {
                config.add(HostAndPort.from(address));
            }
            client = new UnifiedJedis(config, jedisClientConfig, poolConfig, 3, Duration.ofSeconds(10));
        } else if (mode.equals("sharded")) {
            String nodes = conf.get("redis.nodes", "");
            if (Strings.isBlank(nodes)) {
                throw new NullPointerException("sharded redis nodes configuration didn't find");
            }
            String[] hosts = nodes.split(",");
            List<HostAndPort> config = new ArrayList<>();
            for (String address : hosts) {
                config.add(HostAndPort.from(address));
            }
            ShardedConnectionProvider provider = new ShardedConnectionProvider(config, jedisClientConfig, poolConfig);
            client = new UnifiedJedis(provider);
        }
        return client;
    }
}
