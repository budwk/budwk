package com.budwk.starter.dubbo.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReferenceBean<T> extends ReferenceConfig<T> {

    private static final long serialVersionUID = -5187407620002307061L;

    public Ioc ioc;

    public String beanName;

    public ReferenceBean() {
        super();
    }

    public ReferenceBean(DubboReference reference) {
        super();
        // 手动设置 DubboReference 注解的配置
        if (reference != null) {
            if (reference.interfaceClass() != void.class) {
                setInterface(reference.interfaceClass());
            }
            if (!"".equals(reference.interfaceName())) {
                setInterface(reference.interfaceName());
            }
            if (!"".equals(reference.version())) {
                setVersion(reference.version());
            }
            if (!"".equals(reference.group())) {
                setGroup(reference.group());
            }
            if (!"".equals(reference.url())) {
                setUrl(reference.url());
            }
            setCheck(reference.check());
            setInit(reference.init());
            setLazy(reference.lazy());
            // 其他属性设置...
        }
    }

    /**
     * IOC初始化方法
     */
    public void _init() {
        try {
            log.debug("Initializing reference: {}", beanName);

            // 设置消费者配置
            if (ioc.has("consumerConfig")) {
                ConsumerConfig consumerConfig = ioc.get(ConsumerConfig.class, "consumerConfig");
                setConsumer(consumerConfig);
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

            // 检查是否需要立即初始化
//            Boolean shouldInit = getInit();
//            if (shouldInit == null && getConsumer() != null) {
//                shouldInit = getConsumer().getInit();
//            }
//            if (shouldInit != null && shouldInit.booleanValue()) {
//                get();
//            }

            log.debug("Reference initialized: {}", beanName);
        } catch (Exception e) {
            log.error("Failed to initialize reference: " + beanName, e);
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * IOC销毁方法
     */
    public void depose() {
        try {
            log.debug("Disposing reference: {}", beanName);
            this.destroy();
        } catch (Exception e) {
            log.error("Failed to dispose reference: " + beanName, e);
        }
    }

    @Override
    public void destroy() {
        try {
            super.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy reference config", e);
        }
    }
}