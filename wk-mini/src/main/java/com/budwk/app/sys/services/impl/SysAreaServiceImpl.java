package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_area;
import com.budwk.app.sys.services.SysAreaService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_area", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class SysAreaServiceImpl extends BaseServiceImpl<Sys_area> implements SysAreaService {
    public SysAreaServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    @CacheResult
    public List<Sys_area> getSubListByCode(String code) {
        if (Strings.isNotBlank(code)) {
            Sys_area dict = this.fetch(Cnd.where("code", "=", code));
            return dict == null ? new ArrayList<>() : this.query(Cnd.where("parentId", "=", Strings.sNull(dict.getId())).asc("location"));
        } else {
            return this.query(Cnd.where("parentId", "=", "").asc("location"));
        }
    }

    @Override
    @CacheResult
    public List<Sys_area> getSubListByCode(String filedName, String code) {
        if (Strings.isNotBlank(code)) {
            Sys_area dict = this.fetch(Cnd.where("code", "=", code));
            return dict == null ? new ArrayList<>() : this.query(filedName, Cnd.where("parentId", "=", Strings.sNull(dict.getId())).asc("location"));
        } else {
            return this.query(filedName, Cnd.where("parentId", "=", "").asc("location"));
        }
    }

    @Override
    @CacheResult
    public NutMap getSubMapByCode(String code) {
        if (Strings.isNotBlank(code)) {
            Sys_area dict = this.fetch(Cnd.where("code", "=", code));
            return dict == null ? NutMap.NEW() : this.getNutMap(Sqls.create("select code,name from sys_area where parentId = @id order by location asc").setParam("id", dict.getId()));
        } else {
            return this.getNutMap(Sqls.create("select code,name from sys_area where parentId = '' order by location asc"));
        }
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void save(Sys_area area, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Sys_area pp = this.fetch(pid);
            path = pp.getPath();
        }
        area.setPath(getSubPath("sys_area", "path", path));
        area.setParentId(pid);
        dao().insert(area);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Sys_area area) {
        dao().execute(Sqls.create("delete from sys_area where path like @path").setParam("path", area.getPath() + "%"));
        if (!Strings.isEmpty(area.getParentId())) {
            int count = count(Cnd.where("parentId", "=", area.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_area set hasChildren=0 where id=@pid").setParam("pid", area.getParentId()));
            }
        }
    }

    @Override
    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
