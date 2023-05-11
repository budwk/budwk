package com.budwk.app.device.message.utils;

import com.budwk.app.device.message.MqMessage;
import org.nustaq.serialization.FSTConfiguration;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
public class FastSerializeUtil {
    private static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

    public static <T extends Serializable> byte[] serialize(MqMessage<T> mqMessage) {
        return fstConfiguration.asByteArray(mqMessage);
    }

    public static <T extends Serializable> MqMessage<T> deserialize(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        return (MqMessage<T>) fstConfiguration.asObject(bytes);
    }
}
