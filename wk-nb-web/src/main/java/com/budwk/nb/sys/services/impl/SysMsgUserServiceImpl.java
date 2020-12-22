package com.budwk.nb.sys.services.impl;

import com.budwk.nb.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_msg_user;
import com.budwk.nb.sys.services.SysMsgUserService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;

import static com.budwk.nb.base.constant.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "sys_msg_user", cacheLiveTime = 300)
public class SysMsgUserServiceImpl extends BaseServiceImpl<Sys_msg_user> implements SysMsgUserService {
    public SysMsgUserServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 获取未读消息数量
     *
     * @param loginname
     * @return
     */
    @Override
    @CacheResult(cacheKey = "${loginname}_getUnreadNum")
    public int getUnreadNum(String loginname) {
        int size = this.count(Cnd.where("delFlag", "=", false).and("loginname", "=", loginname)
                .and("status", "=", 0));
        return size;
    }

    /**
     * 获取未读消息列表
     *
     * @param loginname
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    @CacheResult(cacheKey = "${loginname}_getUnreadList")
    public List<Sys_msg_user> getUnreadList(String loginname, int pageNumber, int pageSize) {
        return this.query(Cnd.where("delFlag", "=", false).and("loginname", "=", loginname)
                .and("status", "=", 0)
                .desc("createdAt"), "msg", Cnd.orderBy().desc("sendAt"), new Pager().setPageNumber(pageNumber).setPageSize(pageSize));
    }

    /**
     * 可以通过el表达式加 * 通配符来批量删除一批缓存
     *
     * @param loginname 用户名
     */
    @Override
    @CacheRemove(cacheKey = "${loginname}_*")
    @Async
    public void deleteCache(String loginname) {

    }

    @Override
    @CacheRemoveAll
    @Async
    public void clearCache() {

    }
}
