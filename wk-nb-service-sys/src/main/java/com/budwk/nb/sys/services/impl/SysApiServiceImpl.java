package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_api;
import com.budwk.nb.sys.services.SysApiService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;


/**
 * @author wizzer(wizzer @ qq.com) on 2018/3/16.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysApiService.class)
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "sys_api")
public class SysApiServiceImpl extends BaseServiceImpl<Sys_api> implements SysApiService {
    public SysApiServiceImpl(Dao dao) {
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
    public void createAppkey(String name, String userId) throws Exception {
        String appid = getAppid();
        Sys_api sysApi = new Sys_api();
        sysApi.setName(name);
        sysApi.setDisabled(false);
        sysApi.setAppid(appid);
        sysApi.setAppkey(R.sg(30).next().replaceAll("_", "z"));
        sysApi.setCreatedBy(userId);
        sysApi.setUpdatedBy(userId);
        this.insert(sysApi);
        // 调用一下以便生成缓存
        this.getAppkey(appid);
    }

    @Override
    public void deleteAppkey(String appid) throws Exception {
        this.delete(appid);
        this.deleteCache(appid);
    }

    @Override
    public void updateAppkey(String appid, boolean disabled, String userId) throws Exception {
        this.update(Chain.make("disabled", disabled).add("updatedBy", userId).add("updatedAt", Times.now().getTime()), Cnd.where("appid", "=", appid));
        this.deleteCache(appid);
        // 调用一下以便生成缓存
        this.getAppkey(appid);
    }

    /**
     * 注意这个cacheKey 是和 web-api 对应一致的,便于直接从redis取值,而不用依赖sys模块
     *
     * @param appid appid
     * @return
     */
    @Override
    @CacheResult(cacheKey = "${appid}_appkey")
    public String getAppkey(String appid) {
        Sys_api sysApi = this.fetch(Cnd.where("appid", "=", appid).and("disabled", "=", false));
        if (sysApi != null) {
            return sysApi.getAppkey();
        }
        return "";
    }

    @Override
    @CacheRemove(cacheKey = "${appid}_*")
    @Async
    //可以通过el表达式加 * 通配符来批量删除一批缓存
    public void deleteCache(String appid) {

    }

    @Override
    @CacheRemoveAll
    @Async
    public void clearCache() {

    }
}
