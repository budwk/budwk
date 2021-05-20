package com.budwk.app.sys.providers;

/**
 * 系统密钥
 *
 * @author wizzer@qq.com
 */
public interface ISysKeyProvider {
    /**
     * 获取appkey
     *
     * @param appid appid
     * @return
     */
    String getAppkey(String appid);
}
