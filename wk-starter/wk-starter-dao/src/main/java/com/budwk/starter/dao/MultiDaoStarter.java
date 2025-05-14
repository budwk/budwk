package com.budwk.starter.dao;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.jdbc.DataSourceStarter;
import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
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

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

@IocBean(create = "init", depose = "close")
public class MultiDaoStarter {
    private static final Log log = Logs.get();

    protected static final String PRE = "nutz.dao.";

    @PropDoc(value = "是否打印dao的SQL日志", defaultValue = "true", type = "boolean")
    public static final String PROP_INTERCEPTOR_LOG_ENABLE = PRE + "interceptor.log.enable";

    @PropDoc(value = "是否打印dao的SQL耗时日志", defaultValue = "false", type = "boolean")
    public static final String PROP_INTERCEPTOR_TIME_ENABLE = PRE + "interceptor.time.enable";

    @PropDoc(value = "sql目录", defaultValue = "sqls/")
    public static final String PROP_SQLS_PATH = PRE + "sqls.path";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    private static String DEFAULT_DATASOURCE = "A";
    // 存储主库数据源
    private Map<String, DataSource> masterDataSources = new HashMap<>();
    // 存储从库数据源
    private Map<String, DataSource> slaveDataSources = new HashMap<>();

    private NacosConfigService nacosConfigService;
    private Listener configListener;

    public void init() {
        DEFAULT_DATASOURCE = conf.get("jdbc.default", DEFAULT_DATASOURCE);
        if ("da".equalsIgnoreCase(conf.get("jdbc.mode"))) {
            log.info("双活数据源模式..");
            initDADataSource();
            if (conf.getBoolean("da.nacos.enable", false)) {
                registerNacosConfigListener();
            }
            injectDADao();
        } else if ("many".equalsIgnoreCase(conf.get("jdbc.mode"))) {
            log.info("多数据源模式..");
            injectDao();
            injectManyDao();
        } else {
            log.info("单数据源模式..");
            injectDao();
        }
    }

    /**
     * 注入Dao
     */
    private void injectDao() {
        DataSource dataSource = DataSourceStarter.createManyDataSource(ioc, conf, "jdbc.");
        NutDao dao = new NutDao();
        dao.setDataSource(dataSource);
        dao.setSqlManager(ioc.get(SqlManager.class));
        List<Object> interceptors = new ArrayList<>();
        if (conf.getBoolean(PROP_INTERCEPTOR_LOG_ENABLE, true)) {
            interceptors.add("log");
        }
        // sql耗时拦截器
        if (conf.getBoolean(PROP_INTERCEPTOR_TIME_ENABLE, false)) {
            interceptors.add("time");
        }
        // 将拦截器赋予dao对象
        dao.setInterceptors(interceptors);
        // 看看是不是需要注入从数据库
        if (conf.has("jdbc.slave.url")) {
            DataSource slaveDataSource = DataSourceStarter.getSlaveDataSource(ioc, conf, "jdbc.slave.");
            if (slaveDataSource != null) {
                NutDaoRunner runner = new NutDaoRunner();
                runner.setSlaveDataSource(slaveDataSource);
                dao.setRunner(runner);
            }
        }
        ioc.addBean("dao", dao);
    }


    @IocBean
    public SqlManager getSqlManager() {
        return new FileSqlManager(conf.get("nutz.dao.sqls.path", "sqls/"));
    }

    private void initDADataSource() {
        // 正则匹配多数据库url
        String regex = "jdbc\\.many\\.(\\w*)\\.url";
        for (String key : conf.getKeys()) {
            Pattern pattern = Regex.getPattern(regex);
            Matcher match = pattern.matcher(key);
            if (match.find()) {
                // 获取数据库名称
                String name = match.group(1);
                String prefix_name = "jdbc.many." + name + ".";
                try {
                    masterDataSources.put(name, DataSourceStarter.createManyDataSource(ioc, conf, prefix_name));
                    // 处理对应的从库
                    String slave_prefix = prefix_name + "slave.";
                    if (conf.has(slave_prefix + "url")) {
                        slaveDataSources.put(name, DataSourceStarter.createManyDataSource(ioc, conf, slave_prefix));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("datasource init error " + prefix_name, e);
                }
            }
        }
    }

    private void injectDADao() {
        NutDao nutDao = new NutDao();
        nutDao.setSqlManager(getSqlManager());
        List<Object> interceptors = new ArrayList<>();
        if (conf.getBoolean(PROP_INTERCEPTOR_LOG_ENABLE, true)) {
            interceptors.add("log");
        }
        // sql耗时拦截器
        if (conf.getBoolean(PROP_INTERCEPTOR_TIME_ENABLE, false)) {
            interceptors.add("time");
        }
        // 将拦截器赋予dao对象
        nutDao.setInterceptors(interceptors);
        // 看看是不是需要注入从数据库
        nutDao.setDataSource(masterDataSources.get(DEFAULT_DATASOURCE));
        if (slaveDataSources.get(DEFAULT_DATASOURCE) != null) {
            NutDaoRunner runner = new NutDaoRunner();
            runner.setSlaveDataSource(slaveDataSources.get(DEFAULT_DATASOURCE));
            nutDao.setRunner(runner);
        }
        ioc.addBean("dao", nutDao);
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
                log.debug("当前数据源: " + da);
            }
            configListener = new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("nacos configInfo: " + configInfo);
                    NutMap nutMap = Json.fromJson(NutMap.class, configInfo);
                    String da = nutMap.getString("datasource");
                    if (Strings.isNotBlank(da) && !da.equalsIgnoreCase(getCurrentDatasource())) {
                        NutDao dao = ioc.get(NutDao.class, "dao");
                        dao.setDataSource(masterDataSources.get(da));
                        if (slaveDataSources.get(da) != null) {
                            NutDaoRunner runner = new NutDaoRunner();
                            runner.setSlaveDataSource(slaveDataSources.get(da));
                            dao.setRunner(runner);
                        }
                        log.infof("切换数据源为：%s", da);
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

    private void injectManyDao() {
        // 正则匹配多数据库url
        String regex = "jdbc\\.many\\.(\\w*)\\.url";
        for (String key : conf.getKeys()) {
            Pattern pattern = Regex.getPattern(regex);
            Matcher match = pattern.matcher(key);
            if (match.find()) {
                // 获取数据库名称
                String name = match.group(1);
                String prefix_name = "jdbc.many." + name + ".";
                try {
                    DataSource manyDataSource = DataSourceStarter.createManyDataSource(ioc, conf, prefix_name);
                    NutDao nutDao = new NutDao();
                    nutDao.setDataSource(manyDataSource);
                    // 处理对应的从库
                    String slave_prefix = prefix_name + "slave.";
                    DataSource slaveDataSource = DataSourceStarter.getManySlaveDataSource(ioc, conf, slave_prefix);
                    if (slaveDataSource != null) {
                        NutDaoRunner runner = new NutDaoRunner();
                        runner.setSlaveDataSource(slaveDataSource);
                        nutDao.setRunner(runner);
                    }
                    // 加入到ioc对象
                    ioc.addBean(name + "Dao", nutDao);
                    // 设置默认dao
                    if (name.equalsIgnoreCase(getCurrentDatasource())) {
                        ioc.addBean("dao", nutDao);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("datasource init error " + prefix_name, e);
                }
            }
        }
    }

    public Properties getNacosConfigProperties() {
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
    public static void setCurrentDatasource(String datasource) {
        DEFAULT_DATASOURCE = datasource;
    }

    /**
     * 获取当前使用的数据源名称
     *
     * @return 数据源名称，A或B
     */
    public static String getCurrentDatasource() {
        return DEFAULT_DATASOURCE;
    }

    /**
     * 清除线程本地变量，防止内存泄漏
     * 注意：应在使用完毕后调用此方法，特别是在线程池环境下
     */
    public void close() {
        if (nacosConfigService != null && configListener != null) {
            nacosConfigService.removeListener(conf.get("da.nacos.data-id"), conf.get("da.nacos.group", "DEFAULT_GROUP"), configListener);
        }
    }
}
