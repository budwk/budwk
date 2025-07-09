package com.budwk.starter.dubbo.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Slf4j
public class ServiceBean<T> extends ServiceConfig<T> {


    public Ioc ioc;

    public String beanName;

    public ServiceBean() {
        super();
    }

    public ServiceBean(DubboService service) {
        super();
        // 手动设置 DubboService 注解的配置
        if (service != null) {
            if (service.interfaceClass() != void.class) {
                setInterface(service.interfaceClass());
            }
            if (!"".equals(service.interfaceName())) {
                setInterface(service.interfaceName());
            }
            if (!"".equals(service.version())) {
                setVersion(service.version());
            }
            if (!"".equals(service.group())) {
                setGroup(service.group());
            }
            if (!"".equals(service.path())) {
                setPath(service.path());
            }
            setExport(service.export());
            // 其他属性设置...
        }
    }

    /**
     * IOC初始化方法
     */
    public void _init() {
        try {
            log.debug("Initializing service: {}", beanName);

            // 设置提供者配置
            if (ioc.has("providerConfig")) {
                ProviderConfig providerConfig = ioc.get(ProviderConfig.class, "providerConfig");
                setProvider(providerConfig);
            }

            // 设置应用配置 - 添加 try-catch 保护
            if (ioc.has("applicationConfig")) {
                ApplicationConfig applicationConfig = ioc.get(ApplicationConfig.class, "applicationConfig");
                setApplication(applicationConfig);
            }

            // 设置模块配置
            if (ioc.has("moduleConfig")) {
                ModuleConfig moduleConfig = ioc.get(ModuleConfig.class, "moduleConfig");
                setModule(moduleConfig);
            }

            // 设置注册中心配置
            if (ioc.has("registryConfig")) {
                RegistryConfig registryConfig = ioc.get(RegistryConfig.class, "registryConfig");
                List<RegistryConfig> registryConfigs = new ArrayList<>();
                registryConfigs.add(registryConfig);
                setRegistries(registryConfigs);
            }

            // 设置监控配置
            if (ioc.has("monitorConfig")) {
                MonitorConfig monitorConfig = ioc.get(MonitorConfig.class, "monitorConfig");
                setMonitor(monitorConfig);
            }

            // 设置协议配置
            if (ioc.has("protocolConfig")) {
                ProtocolConfig protocolConfig = ioc.get(ProtocolConfig.class, "protocolConfig");
                List<ProtocolConfig> protocolConfigs = new ArrayList<>();
                protocolConfigs.add(protocolConfig);
                setProtocols(protocolConfigs);
            }

            // 设置路径
            if (getPath() == null || getPath().length() == 0) {
                if (beanName != null && beanName.length() > 0
                        && getInterface() != null && getInterface().length() > 0
                        && beanName.startsWith(getInterface())) {
                    setPath(beanName);
                }
            }

            // 导出服务
            export();

            log.debug("Service exported: {}", beanName);
        } catch (Exception e) {
            log.error("Failed to initialize service: " + beanName, e);
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * IOC销毁方法
     */
    public void depose() {
        try {
            log.debug("Disposing service: {}", beanName);
            this.unexport();
        } catch (Exception e) {
            log.error("Failed to dispose service: " + beanName, e);
        }
    }
}