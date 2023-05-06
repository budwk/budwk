package com.budwk.app.device.handler.common.codec.context;

import com.budwk.app.device.handler.common.codec.CacheStore;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.device.CommandInfo;

/**
 * 编码上下文
 * @author wizzer.cn
 */
public interface EncodeContext {
    /**
     * 指令信息
     */
    CommandInfo getCommandInfo();

    /**
     * 设备信息
     *
     * @return
     */
    DeviceOperator getDeviceOperator();

    /**
     * 缓存服务
     *
     * @param id
     * @return
     */
    CacheStore getCacheStore(String id);
}
