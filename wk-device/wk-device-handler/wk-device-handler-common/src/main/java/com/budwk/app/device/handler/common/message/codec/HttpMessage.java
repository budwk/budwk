package com.budwk.app.device.handler.common.message.codec;

import com.budwk.app.device.handler.common.enums.TransportType;
import lombok.Data;

import java.util.Map;

/**
 * @author wizzer.cn
 * @author zyang
 */
@Data
public class HttpMessage implements EncodedMessage {

    private static final long serialVersionUID = 1588297065657636014L;

    private String platform;
    private byte[] payload;

    private Map<String, String> header;
    private String method;
    private String url;
    private String path;
    private Map<String, String> query;

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "HttpMessage{" +
                ", platform='" + platform + '\'' +
                ", payload=" + payloadAsString() +
                ", header=" + header +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", query=" + query +
                '}';
    }
}
