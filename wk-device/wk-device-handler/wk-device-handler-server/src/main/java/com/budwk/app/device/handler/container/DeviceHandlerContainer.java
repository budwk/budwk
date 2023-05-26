package com.budwk.app.device.handler.container;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.task.DelayTaskHelper;
import com.budwk.app.device.providers.IDeviceHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
@Slf4j
public class DeviceHandlerContainer {
    @Inject
    @Reference(check = false)
    private IDeviceHandlerProvider deviceHandlerProvider;
    @Inject
    private RedissonClient redissonClient;
    @Inject
    private DelayTaskHelper delayTaskHelper;
    /**
     * 存放会话ID-设备映射
     */
    private static final Map<String, DeviceOperator> sessionDevice = new ConcurrentHashMap<>();

    /**
     * 存放设备ID-会话信息映射
     */
    private RMapCache<String, NutMap> deviceSessionCache;

    /**
     * 缓存
     */
    private RMapCache<String, Object> cacheMap;

    public void init() {
        // 初始化缓存
        cacheMap = redissonClient.getMapCache("protocol_container");
        deviceSessionCache = redissonClient.getMapCache("device_addr");
    }
}
