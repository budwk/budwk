package com.budwk.app.sys.providers;

import org.nutz.lang.util.NutMap;

/**
 * 系统参数
 * @author wizzer@qq.com
 */
public interface ISysConfigProvider {
    /**
     * 获取应用及公共的系统参数
     *
     * @param appId 应用ID
     * @return
     */
    NutMap getMapAll(String appId);

    /**
     * 获取应用及公共的系统参数（开放的）
     *
     * @param appId 应用ID
     * @return
     */
    NutMap getMapOpened(String appId);

    /**
     * 获取字符串配置项
     *
     * @param appId 应用ID
     * @param key   Key
     * @return
     */
    String getString(String appId, String key);

    /**
     * 获取布尔型配置项
     *
     * @param appId 应用ID
     * @param key   Key
     * @return
     */
    boolean getBoolean(String appId, String key);
}
