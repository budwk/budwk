package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.enums.SysConfigType;
import com.budwk.app.sys.models.Sys_config;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.app.sys.services.SysConfigService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_config", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class SysConfigServiceImpl extends BaseServiceImpl<Sys_config> implements SysConfigService {
    public SysConfigServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysAppService sysAppService;

    @Override
    @CacheResult(cacheKey = "${appId}_getMapAll")
    public NutMap getMapAll(String appId) {
        NutMap nutMap = NutMap.NEW();
        List<Sys_config> commonConfig = this.query(Cnd.where("appId", "=", GlobalConstant.DEFAULT_COMMON_APPID));
        for (Sys_config config : commonConfig) {
            if (SysConfigType.BOOL == config.getType()) {
                nutMap.put(config.getConfigKey(), Boolean.valueOf(config.getConfigValue()));
            } else {
                nutMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        List<Sys_config> appConfig = this.query(Cnd.where("appId", "=", appId));
        for (Sys_config config : appConfig) {
            if (SysConfigType.BOOL == config.getType()) {
                nutMap.put(config.getConfigKey(), Boolean.valueOf(config.getConfigValue()));
            } else {
                nutMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return nutMap;
    }

    @Override
    @CacheResult(cacheKey = "${appId}_getMapOpened")
    public NutMap getMapOpened(String appId) {
        NutMap nutMap = NutMap.NEW();
        List<Sys_config> commonConfig = this.query(Cnd.where("appId", "=", GlobalConstant.DEFAULT_COMMON_APPID)
                .and("opened", "=", true));
        for (Sys_config config : commonConfig) {
            if (SysConfigType.BOOL == config.getType()) {
                nutMap.put(config.getConfigKey(), Boolean.valueOf(config.getConfigValue()));
            } else {
                nutMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        List<Sys_config> appConfig = this.query(Cnd.where("appId", "=", appId)
                .and("opened", "=", true));
        for (Sys_config config : appConfig) {
            if (SysConfigType.BOOL == config.getType()) {
                nutMap.put(config.getConfigKey(), Boolean.valueOf(config.getConfigValue()));
            } else {
                nutMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return nutMap;
    }

    @Override
    public String getString(String appId, String key) {
        return getMapAll(appId).getString(key,"");
    }

    @Override
    public boolean getBoolean(String appId, String key) {
        return getMapAll(appId).getBoolean(key,false);
    }

    @Override
    @CacheRemove(cacheKey = "${appId}_*")
    public void cacheRemove(String appId) {

    }

    @Override
    @CacheRemoveAll
    public void cacheClear() {

    }
}
