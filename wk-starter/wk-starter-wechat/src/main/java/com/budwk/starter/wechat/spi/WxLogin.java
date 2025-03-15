package com.budwk.starter.wechat.spi;

public interface WxLogin {

    /**
     * 返回重定向到微信登录页面的URL
     * @param state
     * @return
     */
    String qrconnect(String redirect_uri, String scope, String state);

    String authorize(String redirect_uri, String scope, String state);
    
    /**
     * 根据code换取access_token
     * @param code
     * @param state
     * @return
     */
    WxResp access_token(String auth_code); 
    
    /**
     * 刷新token
     * @param token
     * @return
     */
    WxResp refresh_token(String refresh_token);
    
    /**
     * 验证token是否还有效
     * @param token
     * @return
     */
    WxResp auth(String token);
    
    /**
     * 获取用户信息
     * @param openid
     * @param token
     * @return
     */
    WxResp userinfo(String openid, String token);
}
