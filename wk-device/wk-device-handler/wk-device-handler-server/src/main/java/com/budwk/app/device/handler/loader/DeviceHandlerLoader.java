package com.budwk.app.device.handler.loader;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.handler.common.codec.Handler;
import com.budwk.app.device.handler.common.codec.HandlerLoader;
import com.budwk.app.device.objects.dto.DeviceHandlerDTO;
import com.budwk.app.device.providers.IDeviceHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wizzer.cn
 */
@Slf4j
@IocBean
public class DeviceHandlerLoader implements HandlerLoader {

    @Inject
    @Reference
    private IDeviceHandlerProvider deviceHandlerProvider;

    private final ConcurrentHashMap<String, ClassLoader> classLoaders = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Handler> loadHandlers = new ConcurrentHashMap<>();

    @Override
    public Handler loadHandler(String handlerCode) {
        try {
            // 如果已经加载过了就直接返回
            Handler handler = loadHandlers.get(handlerCode);
            if (null != handler) {
                return handler;
            }
            // 从数据库查询解析包记录
            DeviceHandlerDTO dto = deviceHandlerProvider.getHandler(handlerCode);
            if (null == dto || Strings.isBlank(dto.getFileUrl()) || Strings.isBlank(dto.getMainClass())) {
                return null;
            }
            // 通过 URLClassLoader 加载
            URL[] urls = {
                    new URL(dto.getFileUrl())
            };
            ClassLoader loader = classLoaders.computeIfAbsent(handlerCode, k -> URLClassLoader.newInstance(urls, this.getClass().getClassLoader()));
            // 实例化
            Class<?> clazz = loader.loadClass(dto.getMainClass());
            if (Handler.class.isAssignableFrom(clazz)) {
                handler = (Handler) clazz.getConstructor().newInstance();
                loadHandlers.put(handlerCode, handler);
            }
            return handler;
        } catch (Exception e) {
            log.error("加载协议包出错", e);
        }
        return null;
    }

    @Override
    public void reload(String handlerCode) {
        loadHandlers.remove(handlerCode);
        classLoaders.remove(handlerCode);
        this.loadHandler(handlerCode);
    }
}
