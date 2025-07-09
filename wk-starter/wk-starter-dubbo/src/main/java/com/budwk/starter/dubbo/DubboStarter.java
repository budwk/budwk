package com.budwk.starter.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.*;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(create = "init", depose = "depose")
@Slf4j
public class DubboStarter implements ServerFace {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @PropDoc(value = "Dubbo应用名称", defaultValue = "budwk-dubbo-app")
    public static final String PROP_DUBBO_APPLICATION_NAME = "dubbo.application.name";

    @PropDoc(value = "Dubbo协议名称", defaultValue = "dubbo")
    public static final String PROP_DUBBO_PROTOCOL_NAME = "dubbo.protocol.name";

    @PropDoc(value = "Dubbo协议端口", defaultValue = "20880")
    public static final String PROP_DUBBO_PROTOCOL_PORT = "dubbo.protocol.port";

    @PropDoc(value = "Dubbo注册中心地址", defaultValue = "")
    public static final String PROP_DUBBO_REGISTRY_ADDRESS = "dubbo.registry.address";

    @PropDoc(value = "Dubbo消费者超时时间(毫秒)", defaultValue = "3000")
    public static final String PROP_DUBBO_CONSUMER_TIMEOUT = "dubbo.consumer.timeout";

    @PropDoc(value = "Dubbo消费者重试次数", defaultValue = "0")
    public static final String PROP_DUBBO_CONSUMER_RETRIES = "dubbo.consumer.retries";

    @PropDoc(value = "Dubbo消费者负载均衡策略", defaultValue = "random")
    public static final String PROP_DUBBO_CONSUMER_LOADBALANCE = "dubbo.consumer.loadbalance";

    @PropDoc(value = "Dubbo消费者集群容错策略", defaultValue = "failover")
    public static final String PROP_DUBBO_CONSUMER_CLUSTER = "dubbo.consumer.cluster";

    @PropDoc(value = "是否检查提供者存在", defaultValue = "true")
    public static final String PROP_DUBBO_CONSUMER_CHECK = "dubbo.consumer.check";

    @PropDoc(value = "Dubbo提供者超时时间(毫秒)", defaultValue = "3000")
    public static final String PROP_DUBBO_PROVIDER_TIMEOUT = "dubbo.provider.timeout";

    @PropDoc(value = "服务延迟暴露时间(毫秒)", defaultValue = "-1")
    public static final String PROP_DUBBO_PROVIDER_DELAY = "dubbo.provider.delay";

    @PropDoc(value = "Dubbo扫描包路径，多个包用逗号分隔", defaultValue = "")
    public static final String PROP_DUBBO_SCAN_PACKAGES = "dubbo.scan.packages";

    @PropDoc(value = "Dubbo监控中心地址", defaultValue = "")
    public static final String PROP_DUBBO_MONITOR_ADDRESS = "dubbo.monitor.address";

    @PropDoc(value = "Dubbo服务分组", defaultValue = "")
    public static final String PROP_DUBBO_SERVICE_GROUP = "dubbo.service.group";

    @PropDoc(value = "Dubbo服务版本", defaultValue = "")
    public static final String PROP_DUBBO_SERVICE_VERSION = "dubbo.service.version";

    /**
     * 配置代理，直接读取配置文件
     */
    @Inject
    protected PropertiesProxy conf;

    /**
     * Dubbo管理器
     */
    protected DubboManager dubboManager;

    protected String name;

    /**
     * 初始化方法
     */
    public void init() throws Exception {
        log.info("Starting Dubbo V3 Simplified Auto Configuration...");

        // 创建并注册 Dubbo 基础配置对象
        createAndRegisterConfigurations();

        // 创建并初始化 Dubbo 管理器
        createAndInitializeDubboManager();

        log.info("Dubbo V3 Simplified Auto Configuration completed");
    }

    /**
     * 创建并注册 Dubbo 配置对象
     */
    private void createAndRegisterConfigurations() {
        // 创建应用配置
        ApplicationConfig applicationConfig = createApplicationConfig();
        if (applicationConfig != null) {
            ioc.addBean("applicationConfig", applicationConfig);
        }

        // 创建协议配置
        ProtocolConfig protocolConfig = createProtocolConfig();
        if (protocolConfig != null) {
            ioc.addBean("protocolConfig", protocolConfig);
        }

        // 创建注册中心配置
        RegistryConfig registryConfig = createRegistryConfig();
        if (registryConfig != null) {
            ioc.addBean("registryConfig", registryConfig);
        }

        // 创建消费者配置
        ConsumerConfig consumerConfig = createConsumerConfig();
        if (consumerConfig != null) {
            ioc.addBean("consumerConfig", consumerConfig);
        }

        // 创建提供者配置
        ProviderConfig providerConfig = createProviderConfig();
        if (providerConfig != null) {
            ioc.addBean("providerConfig", providerConfig);
        }

        // 创建监控配置
        MonitorConfig monitorConfig = createMonitorConfig();
        if (monitorConfig != null) {
            ioc.addBean("monitorConfig", monitorConfig);
        }

        log.debug("Dubbo configurations created and registered");
    }

    /**
     * 创建应用配置
     */
    private ApplicationConfig createApplicationConfig() {
        String appName = conf.get(PROP_DUBBO_APPLICATION_NAME, conf.get("nutz.application.name", "budwk-dubbo-app"));

        ApplicationConfig config = conf.make(ApplicationConfig.class, "dubbo.application.");
        config.setName(appName);

        log.debug("Created ApplicationConfig: name={}", appName);
        return config;
    }

    /**
     * 创建协议配置
     */
    private ProtocolConfig createProtocolConfig() {
        return conf.make(ProtocolConfig.class,"dubbo.protocol.");
    }

    /**
     * 创建注册中心配置
     */
    private RegistryConfig createRegistryConfig() {
        return conf.make(RegistryConfig.class,"dubbo.registry.");
    }

    /**
     * 创建消费者配置
     */
    private ConsumerConfig createConsumerConfig() {
        ConsumerConfig config = new ConsumerConfig();

        // 设置超时时间
        int timeout = conf.getInt(PROP_DUBBO_CONSUMER_TIMEOUT, 3000);
        config.setTimeout(timeout);

        // 设置重试次数
        int retries = conf.getInt(PROP_DUBBO_CONSUMER_RETRIES, 0);
        config.setRetries(retries);

        // 设置负载均衡策略
        String loadbalance = conf.get(PROP_DUBBO_CONSUMER_LOADBALANCE, "random");
        config.setLoadbalance(loadbalance);

        // 设置集群容错策略
        String cluster = conf.get(PROP_DUBBO_CONSUMER_CLUSTER, "failover");
        config.setCluster(cluster);

        // 设置是否检查提供者
        boolean check = conf.getBoolean(PROP_DUBBO_CONSUMER_CHECK, true);
        config.setCheck(check);

        log.debug("Created ConsumerConfig: timeout={}, retries={}, loadbalance={}, cluster={}, check={}",
                timeout, retries, loadbalance, cluster, check);
        return config;
    }

    /**
     * 创建提供者配置
     */
    private ProviderConfig createProviderConfig() {
        ProviderConfig config = new ProviderConfig();

        // 设置超时时间
        int timeout = conf.getInt(PROP_DUBBO_PROVIDER_TIMEOUT, 3000);
        config.setTimeout(timeout);

        // 设置延迟暴露时间
        int delay = conf.getInt(PROP_DUBBO_PROVIDER_DELAY, -1);
        if (delay >= 0) {
            config.setDelay(delay);
        }

        log.debug("Created ProviderConfig: timeout={}, delay={}", timeout, delay);
        return config;
    }

    /**
     * 创建监控配置
     */
    private MonitorConfig createMonitorConfig() {
        String monitorAddress = conf.get(PROP_DUBBO_MONITOR_ADDRESS);
        if (Strings.isBlank(monitorAddress)) {
            log.debug("No monitor address configured, skip MonitorConfig creation");
            return null;
        }

        MonitorConfig config = new MonitorConfig();
        config.setAddress(monitorAddress);

        log.debug("Created MonitorConfig: address={}", monitorAddress);
        return config;
    }

    /**
     * 创建并初始化 Dubbo 管理器
     */
    private void createAndInitializeDubboManager() {
        try {
            dubboManager = ioc.get(DubboManager.class);
            dubboManager.init();
            log.info("DubboManagerV3 initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize DubboManagerV3", e);
            throw new RuntimeException("Failed to initialize DubboManagerV3", e);
        }
    }

    /**
     * 获取Dubbo管理器
     */
    public DubboManager getDubboManager() {
        return dubboManager;
    }

    @Override
    public void start() throws Exception {
        if (dubboManager != null) {
            log.info("Dubbo services are already started in initialization phase");
        }
    }

    @Override
    public void stop() throws Exception {
        if (dubboManager != null) {
            log.info("Stopping Dubbo services...");
            dubboManager.depose();
        }
    }

    @Override
    public boolean isRunning() {
        return dubboManager != null;
    }

    @Override
    public boolean failsafe() {
        return false;
    }

    /**
     * 销毁方法
     */
    public void depose() throws Exception {
        stop();
    }
}
