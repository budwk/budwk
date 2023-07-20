package com.budwk.app.device.handler.impl;

import com.budwk.app.device.handler.common.codec.CacheStore;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.context.EncodeContext;
import com.budwk.app.device.handler.common.device.CommandInfo;
import lombok.Data;

/**
 * @author wizzer.cn
 */
@Data
public class DefaultEncodeContext implements EncodeContext {
    private final CommandInfo commandInfo;
    private final DeviceOperator deviceOperator;

    @Override
    public CacheStore getCacheStore(String id) {
        return null;
    }
}
