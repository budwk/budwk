package com.budwk.nb.sys.services;

import com.budwk.nb.commons.base.service.BaseService;
import com.budwk.nb.sys.models.Sys_api;

/**
 * @author wizzer(wizzer.cn) on 2018/3/16.
 */
public interface SysApiService extends BaseService<Sys_api> {

    /**
     * 创建密钥
     *
     * @param name   应用名称
     * @param userId appid
     * @throws Exception
     */
    void createAppkey(String name, String userId) throws Exception;

    /**
     * 删除密钥
     *
     * @param appid appid
     * @throws Exception
     */
    void deleteAppkey(String appid) throws Exception;

    /**
     * 启用禁用
     *
     * @param appid    appid
     * @param disabled true为禁用
     * @throws Exception
     */
    void updateAppkey(String appid, boolean disabled, String userId) throws Exception;

    /**
     * 通过appid获取appkey
     *
     * @param appid appid
     * @return Sys_api
     */
    String getAppkey(String appid);

    /**
     * 删除缓存
     *
     * @param appid appid
     */
    void deleteCache(String appid);

    /**
     * 清空缓存
     */
    void clearCache();
}
