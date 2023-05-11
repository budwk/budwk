package com.budwk.app.device.message;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
public class MqMessage<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -1160683315647739543L;
    @Getter
    @Setter(AccessLevel.NONE)
    private Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 目的地址
     */
    private final String adress;
    /**
     * 消息体
     */
    private final T body;
    /**
     * 回复地址
     */
    private String replyAddress;
    /**
     * 发送者的标识
     */
    private String sender;

    public MqMessage<T> addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
