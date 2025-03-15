package com.budwk.starter.wechat.spi;

public interface WxAccessTokenApi {

    void setAccessTokenStore(WxAccessTokenStore ats);

    WxAccessTokenStore getAccessTokenStore();

    
    String getAccessToken();
}
