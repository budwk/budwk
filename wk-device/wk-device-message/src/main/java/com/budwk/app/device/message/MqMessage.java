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
public class MqMessage <T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1626610483526112039L;
    @Getter
    @Setter(AccessLevel.NONE)
    private Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 消息主题
     */
    private final String topic;
    /**
     * 消息体
     */
    private final T body;
    /**
     * 回复主题
     */
    private String replyTopic;
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
