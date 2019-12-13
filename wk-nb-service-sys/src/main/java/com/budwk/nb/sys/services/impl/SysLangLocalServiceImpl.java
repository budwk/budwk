package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.sys.models.Sys_lang_local;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysLangLocalService.class)
@CacheDefaults(cacheName = "sys_lang_local")
public class SysLangLocalServiceImpl extends BaseServiceImpl<Sys_lang_local> implements SysLangLocalService {
    public SysLangLocalServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysLangService sysLangService;

    @CacheResult
    public List<Sys_lang_local> getLocal() {
        return this.query("^(name|locale)$", Cnd.where("disabled", "=", false).asc("location"));
    }

    @Aop(TransAop.READ_COMMITTED)
    public void clearLocal(String locale) {
        this.clear(Cnd.where("locale", "=", locale));
        sysLangService.clear(Cnd.where("locale", "=", locale));
    }

    @CacheResult
    public String getLocalJson() {
        return Json.toJson(this.query(Cnd.where("disabled", "=", false).asc("location")),
                JsonFormat.full().setActived("locale|name"));
    }

    @CacheRemoveAll
    public void clearCache() {

    }
}
