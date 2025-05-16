package com.budwk.starter.redis;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Regex;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

@IocBean(create = "init", depose = "depose")
public class RedisStarter {
    private static final Log log = Logs.get();
    @Inject
    private PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    private static String DEFAULT_DATASOURCE = "A";
    private Map<String, UnifiedJedis> masterDataSources = new HashMap<>();

    private NacosConfigService nacosConfigService;
    private Listener configListener;

    public UnifiedJedis getUnifiedJedis() {
        return masterDataSources.get(DEFAULT_DATASOURCE);
    }

    public void init() {
        DEFAULT_DATASOURCE = conf.get("redis.default", DEFAULT_DATASOURCE);
        if ("da".equalsIgnoreCase(conf.get("redis.mode"))) {
            log.info("Redis 双机房模式..");
            initDAClient();
            if (conf.getBoolean("da.nacos.enable", false)) {
                registerNacosConfigListener();
            }
            log.debug(masterDataSources.size());
        } else {
            log.info("Redis 单机房模式..");
            injectUnifiedJedis();
        }
    }

    /**
     * 多机房模式创建Redis客户端
     */
    private void initDAClient() {
        // 正则匹配多数据库url
        String regex = "redis\\.many\\.(\\w*)\\.host";
        for (String key : conf.getKeys()) {
            Pattern pattern = Regex.getPattern(regex);
            Matcher match = pattern.matcher(key);
            if (match.find()) {
                // 获取数据库名称
                String name = match.group(1);
                String prefix_name = "redis.many." + name + ".";
                try {
                    String host = conf.get(prefix_name + "host", "127.0.0.1");
                    int port = conf.getInt(prefix_name + "port", 6379);
                    String user = conf.get(prefix_name + "user", null);
                    String password = conf.get(prefix_name + "password", null);
                    int database = conf.getInt(prefix_name + "database", 0);
                    int maxTotal = conf.getInt(prefix_name + "maxTotal", 8);
                    int maxIdle = conf.getInt(prefix_name + "maxIdle", 8);
                    String mode = conf.get(prefix_name + "mode");

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
                    if ("normal".equalsIgnoreCase(mode)) {
                        HostAndPort config = new HostAndPort(host, port);
                        ConnectionFactory factory = new ConnectionFactory(config, jedisClientConfig);
                        PooledConnectionProvider provider = new PooledConnectionProvider(factory, poolConfig);
                        client = new UnifiedJedis(provider);
                    } else if ("cluster".equalsIgnoreCase(mode)) {
                        String nodes = conf.get(prefix_name + "nodes", "");
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
                    masterDataSources.put(name, client);
                } catch (Exception e) {
                    throw new RuntimeException("Redis init error " + prefix_name, e);
                }
            }
        }
    }

    /**
     * 注册Nacos配置监听器
     */
    private void registerNacosConfigListener() {
        try {
            nacosConfigService = new NacosConfigService(getNacosConfigProperties());
            String dataId = conf.get("da.nacos.data-id", "jdbc");
            String group = conf.get("da.nacos.group", "DEFAULT_GROUP");
            String dataType = conf.get("da.nacos.data-type", "json");
            // 启动时获取当前数据源配置
            String configInfo = nacosConfigService.getConfig(dataId, group, 5000);
            if (Strings.isNotBlank(configInfo)) {
                NutMap nutMap = Json.fromJson(NutMap.class, configInfo);
                String da = nutMap.getString("datasource");
                setCurrentDatasource(da);
                log.debug("Redis 当前数据源: " + da);
            }
            configListener = new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("Redis configInfo: " + configInfo);
                    NutMap nutMap = Json.fromJson(NutMap.class, configInfo);
                    String da = nutMap.getString("datasource");
                    if (Strings.isNotBlank(da) && !da.equalsIgnoreCase(getCurrentDatasource())) {
                        log.infof("Redis 切换数据源为：%s", da);
                        setCurrentDatasource(da);
                    }
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            };
            nacosConfigService.addListener(dataId, group, configListener);
        } catch (NacosException e) {
            log.error("Failed to register Nacos config listener", e);
        }
    }

    /**
     * 单机房模式创建Redis客户端
     */
    private void injectUnifiedJedis() {
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
        masterDataSources.put(DEFAULT_DATASOURCE, client);
    }

    private Properties getNacosConfigProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, conf.get("da.nacos.server-addr", "10.10.10.10:8848"));
        properties.put(NAMESPACE, conf.get("da.nacos.namespace", "DEFAULT_GROUP"));
        properties.put(ACCESS_KEY, conf.get("da.nacos.accessKey", ""));
        properties.put(SECRET_KEY, conf.get("da.nacos.secretKey", ""));
        properties.put(CONTEXT_PATH, conf.get("da.nacos.contextPath", "/nacos"));
        properties.put(CLUSTER_NAME, conf.get("da.nacos.clusterName", ""));
        properties.put(MAX_RETRY, conf.get("da.nacos.maxRetry", ""));
        properties.put(CONFIG_RETRY_TIME, conf.get("da.nacos.config-retry-time", ""));
        properties.put(ENABLE_REMOTE_SYNC_CONFIG, conf.get("da.nacos.enable-remote-sync-config", "false"));
        properties.put(USERNAME, conf.get("da.nacos.username", ""));
        properties.put(PASSWORD, conf.get("da.nacos.password", ""));
        String endpoint = conf.get("da.nacos.endpoint", "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }
        return properties;
    }

    /**
     * 设置当前使用的数据源
     *
     * @param datasource 数据源名称，A或B
     */
    public void setCurrentDatasource(String datasource) {
        DEFAULT_DATASOURCE = datasource;
    }

    /**
     * 获取当前使用的数据源名称
     *
     * @return 数据源名称，A或B
     */
    public String getCurrentDatasource() {
        return DEFAULT_DATASOURCE;
    }

    /**
     * 清除线程本地变量，防止内存泄漏
     * 注意：应在使用完毕后调用此方法，特别是在线程池环境下
     */
    public void depose() {
        // 遍历masterDataSources
        try {
            for (Map.Entry<String, UnifiedJedis> entry : masterDataSources.entrySet()) {
                String name = entry.getKey();
                UnifiedJedis jedis = entry.getValue();
                if (jedis != null) {
                    jedis.close();
                    log.infof("Redis %s 关闭成功", name);
                }
            }
        }catch (Exception e){
            log.error("Redis 关闭失败", e);
        }
        if (nacosConfigService != null && configListener != null) {
            nacosConfigService.removeListener(conf.get("da.nacos.data-id"), conf.get("da.nacos.group", "DEFAULT_GROUP"), configListener);
        }
    }

}
