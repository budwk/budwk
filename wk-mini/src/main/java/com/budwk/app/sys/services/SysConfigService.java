package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_config;
import com.budwk.starter.database.service.BaseService;
import org.nutz.lang.util.NutMap;

/**
 * @author wizzer@qq.com
 */
public interface SysConfigService extends BaseService<Sys_config> {
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

    /**
     * 删除缓存
     *
     * @param appId 应用ID
     */
    void cacheRemove(String appId);

    /**
     * 清空缓存
     */
    void cacheClear();
}
