package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.app.sys.services.SysUserSecurityService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_user_security", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
@Slf4j
public class SysUserSecurityServiceImpl extends BaseServiceImpl<Sys_user_security> implements SysUserSecurityService {
    public SysUserSecurityServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public void insertOrUpdate(Sys_user_security security) {
        int num = this.count(Cnd.where("id", "=", "MAIN"));
        security.setId("MAIN");
        if (num > 0) {
            this.updateIgnoreNull(security);
        } else {
            this.insert(security);
        }
        this.cacheClear();
    }

    @Override
    @CacheResult(ignoreNull = true)
    public Sys_user_security getWithCache() {
        return this.fetch("MAIN");
    }

    @Override
    @CacheRemoveAll
    public void cacheClear() {

    }
}
