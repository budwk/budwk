package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_key;
import com.budwk.app.sys.services.SysKeyService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_key", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class SysKeyServiceImpl extends BaseServiceImpl<Sys_key> implements SysKeyService {
    public SysKeyServiceImpl(Dao dao) {
        super(dao);
    }

    private String getAppid() {
        String appid = R.sg(16).next().replaceAll("_", "z");
        if (this.count(Cnd.where("appid", "=", appid)) > 0) {
            return getAppid();
        }
        return appid;
    }

    @Override
    public void createAppkey(String name, String userId) throws BaseException {
        String appid = getAppid();
        Sys_key sysKey = new Sys_key();
        sysKey.setName(name);
        sysKey.setDisabled(false);
        sysKey.setAppid(appid);
        sysKey.setAppkey(R.sg(30).next().replaceAll("_", "z"));
        sysKey.setCreatedBy(userId);
        sysKey.setUpdatedBy(userId);
        this.insert(sysKey);
    }

    @Override
    public void deleteAppkey(String appid) throws BaseException {
        this.clear(Cnd.where("appid", "=", appid));
        this.cacheRemove(appid);
    }

    @Override
    public void updateAppkey(String appid, boolean disabled, String userId) throws BaseException {
        this.update(Chain.make("disabled", disabled).add("updatedBy", userId).add("updatedAt", System.currentTimeMillis()), Cnd.where("appid", "=", appid));
        this.cacheRemove(appid);
    }

    @Override
    @CacheResult(cacheKey = "${appid}_getAppkey")
    public String getAppkey(String appid) {
        Sys_key sysApi = this.fetch(Cnd.where("appid", "=", appid).and("disabled", "=", false));
        if (sysApi != null) {
            return sysApi.getAppkey();
        }
        return "";
    }

    @Override
    @Async
    @CacheRemove(cacheKey = "${appid}_*")
    public void cacheRemove(String appid) {

    }

    @Override
    @Async
    @CacheRemoveAll
    public void cacheClear() {

    }
}
