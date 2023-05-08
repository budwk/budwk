package com.budwk.app.device.handler.common.utils.aep.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.nutz.http.Request;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Getter
@Setter
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = -5260748467785113598L;
    protected NutMap header = new NutMap();
    protected NutMap params = new NutMap();
    protected byte[] body;
    private Request.METHOD method;
    /**
     * 默认超时时间 3000 ms
     */
    private int timeout = 3000;

    /**
     * 应用id
     */
    private String appId;
    /**
     * 应用Key
     */
    private String appKey;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 基础 URL
     */
    private String baseUrl = "https://ag-api.ctwing.cn";
    private String path;

    public BaseRequest() {
    }

    public BaseRequest(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        header.put("application", appKey);
        header.put("timestamp", String.valueOf(System.currentTimeMillis()));
        header.put("sdk", "0");
    }

    public BaseRequest(String baseUrl, String appKey, String appSecret) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        header.put("application", appKey);
        header.put("timestamp", String.valueOf(System.currentTimeMillis()));
        header.put("sdk", "0");
    }

    public NutMap getHeader() {
        return header;
    }

    public void setHeader(String key, String value) {
        this.header.put(key, value);
    }

    public NutMap getParams() {
        return params;
    }

    public void setParams(NutMap params) {
        this.params.putAll(params);
    }

    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
