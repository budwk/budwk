package com.budwk.starter.dubbo;

import com.budwk.starter.dubbo.bean.ReferenceBean;
import com.budwk.starter.dubbo.bean.ServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.resource.Scans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@IocBean
public class DubboManager {
    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    private PropertiesProxy conf;

    @Inject
    private AppContext appContext;

    /**
     * 服务配置列表
     */
    protected List<ServiceBean<?>> serviceConfigs = new ArrayList<>();

    /**
     * 引用配置列表
     */
    protected List<ReferenceBean<?>> referenceConfigs = new ArrayList<>();

    /**
     * 初始化方法
     */
    public void init() {
        try {
            log.info("Starting Dubbo 3.x manager with enhanced config support...");
            String scanPackages = conf.get("dubbo.scan.packages", appContext.getPackage());
            // 扫描并处理注解
            if (Strings.isNotBlank(scanPackages)) {
                scanAndProcessAnnotations(scanPackages);
            }

            log.info("Dubbo 3.x manager initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Dubbo manager", e);
            throw new RuntimeException("Failed to initialize Dubbo manager", e);
        }
    }

    /**
     * 扫描并处理注解
     */
    protected void scanAndProcessAnnotations(String scanPackages) {
        String[] packagesToScan = scanPackages.split(",");

        for (String pkg : packagesToScan) {
            pkg = pkg.trim();
            if (Strings.isBlank(pkg)) {
                continue;
            }

            log.debug("Scanning package: {}", pkg);

            for (Class<?> clazz : Scans.me().scanPackage(pkg)) {
                if (clazz.isInterface()) {
                    continue;
                }

                // 处理 @DubboService 注解
                processServiceAnnotation(clazz);

                // 处理 @DubboReference 注解
                processReferenceAnnotations(clazz);
            }
        }

        log.debug("Annotation scanning completed. Found {} services and {} references",
                serviceConfigs.size(), referenceConfigs.size());
    }

    /**
     * 处理服务注解
     */
    protected void processServiceAnnotation(Class<?> clazz) {
        DubboService serviceAnnotation = clazz.getAnnotation(DubboService.class);
        if (serviceAnnotation == null) {
            return;
        }

        try {
            // 从 IOC 容器获取服务实现
            Object serviceImpl = ioc.getByType(clazz);
            if (serviceImpl == null) {
                log.warn("Service implementation not found in IOC: {}", clazz.getName());
                return;
            }

            ServiceBean<Object> serviceConfig = new ServiceBean<>(serviceAnnotation);
            serviceConfig.ioc = ioc;
            serviceConfig.beanName = generateBeanName("dubboService");

            // 设置接口
            if (serviceAnnotation.interfaceClass() != void.class) {
                serviceConfig.setInterface(serviceAnnotation.interfaceClass());
            } else if (!Strings.isBlank(serviceAnnotation.interfaceName())) {
                serviceConfig.setInterface(serviceAnnotation.interfaceName());
            } else {
                // 自动推断接口
                Class<?>[] interfaces = clazz.getInterfaces();
                if (interfaces.length > 0) {
                    serviceConfig.setInterface(interfaces[0]);
                }
            }

            // 设置服务实现
            serviceConfig.setRef(serviceImpl);

            // 初始化服务配置
            serviceConfig._init();
            serviceConfigs.add(serviceConfig);

            log.debug("Exported service: {}", clazz.getName());
        } catch (Exception e) {
            log.error("Failed to process service: " + clazz.getName(), e);
        }
    }

    /**
     * 处理引用注解
     */
    protected void processReferenceAnnotations(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            DubboReference refAnnotation = field.getAnnotation(DubboReference.class);
            if (refAnnotation == null || !field.getType().isInterface()) {
                continue;
            }

            try {
                ReferenceBean<Object> referenceConfig = new ReferenceBean<>(refAnnotation);
                referenceConfig.ioc = ioc;
                referenceConfig.beanName = generateBeanName("dubboRef");

                // 设置接口
                if (refAnnotation.interfaceClass() != void.class) {
                    referenceConfig.setInterface(refAnnotation.interfaceClass());
                } else if (!Strings.isBlank(refAnnotation.interfaceName())) {
                    referenceConfig.setInterface(refAnnotation.interfaceName());
                } else {
                    referenceConfig.setInterface(field.getType());
                }

                // 初始化引用配置
                referenceConfig._init();
                referenceConfigs.add(referenceConfig);

                // 将引用对象注册到IOC容器中，供字段注入使用
                String refBeanName = generateReferenceBeanName(clazz, field);
                try {
                    Object referenceProxy = referenceConfig.get();
                    ioc.addBean(refBeanName, referenceProxy);
                    log.debug("Registered reference proxy: {} -> {}", refBeanName, field.getType().getName());
                } catch (Exception e) {
                    log.warn("Failed to register reference proxy: {}", e.getMessage());
                }

                log.debug("Configured reference: {}.{}", clazz.getName(), field.getName());
            } catch (Exception e) {
                log.error("Failed to process reference: " + clazz.getName() + "." + field.getName(), e);
            }
        }
    }

    /**
     * 生成引用bean名称
     */
    private String generateReferenceBeanName(Class<?> clazz, Field field) {
        return clazz.getSimpleName() + "_" + field.getName() + "_DubboRef";
    }

    /**
     * 销毁方法
     */
    public void depose() {
        log.info("Disposing Dubbo 3.x manager...");

        // 取消导出服务
        for (ServiceBean<?> serviceConfig : serviceConfigs) {
            try {
                serviceConfig.depose();
            } catch (Exception e) {
                log.warn("Failed to dispose service", e);
            }
        }

        // 销毁引用
        for (ReferenceBean<?> referenceConfig : referenceConfigs) {
            try {
                referenceConfig.depose();
            } catch (Exception e) {
                log.warn("Failed to dispose reference", e);
            }
        }

        serviceConfigs.clear();
        referenceConfigs.clear();

        log.info("Dubbo 3.x manager disposed");
    }

    /**
     * 生成唯一的bean名称
     */
    private String generateBeanName(String prefix) {
        return prefix + "_" + R.UU32();
    }

    // Getter methods for configurations
    public List<ServiceBean<?>> getServiceConfigs() {
        return new ArrayList<>(serviceConfigs);
    }

    public List<ReferenceBean<?>> getReferenceConfigs() {
        return new ArrayList<>(referenceConfigs);
    }
}