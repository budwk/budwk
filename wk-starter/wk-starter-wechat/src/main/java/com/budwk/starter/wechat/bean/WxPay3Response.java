package com.budwk.starter.wechat.bean;

import org.nutz.http.Header;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
public class WxPay3Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String body;
    private int status;
    private Header header;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
