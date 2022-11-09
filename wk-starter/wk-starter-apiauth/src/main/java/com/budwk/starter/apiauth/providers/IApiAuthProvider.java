package com.budwk.starter.apiauth.providers;

import com.budwk.starter.apiauth.dto.UserVerifyDTO;

public interface IApiAuthProvider {
    /**
     * 根据AppId 获取 AppKey
     *
     * @param appId appId
     * @return
     */
    String getAppKey(String appId);

}
