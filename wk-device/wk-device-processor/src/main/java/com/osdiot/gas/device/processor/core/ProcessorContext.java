package com.osdiot.gas.device.processor.core;

import com.budwk.app.device.handler.common.codec.impl.DecodeReportResult;
import com.budwk.app.device.objects.cache.DeviceProcessCache;
import lombok.Data;
import org.nutz.castor.Castors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProcessorContext {

    /**
     * 设备缓存
     */
    private final DeviceProcessCache device;
    /**
     * 解析后的数据
     */
    private final DecodeReportResult decodeResult;
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    public ProcessorContext addProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public <T> T getProperty(String key, Class<T> type) {
        return Castors.me().castTo(getProperty(key), type);
    }

    public void addProperties(Map<String, String> values) {
        properties.putAll(values);
    }
}
