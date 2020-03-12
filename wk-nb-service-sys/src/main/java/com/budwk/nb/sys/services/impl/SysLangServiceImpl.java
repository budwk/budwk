package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.sys.models.Sys_lang;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.impl.NutMessageMap;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysLangService.class)
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "sys_lang")
public class SysLangServiceImpl extends BaseServiceImpl<Sys_lang> implements SysLangService {
    public SysLangServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysLangLocalService sysLangLocalService;

    @Override
    @CacheResult
    public NutMessageMap getLang(String locale) {
        return this.queryMessageMap("lang_key", "lang_value", Cnd.where("locale", "=",locale));
    }

    @Override
    @CacheRemoveAll
    public void clearCache() {

    }

    private NutMessageMap queryMessageMap(String keyColumnName, String valueColumnName, Condition cnd) {
        NutMessageMap messageMap = new NutMessageMap();
        List<Record> list = this.dao().query(this.getEntity().getTableName(), cnd);
        for (Record record : list) {
            messageMap.put(Strings.sNull(record.get(keyColumnName)), record.get(valueColumnName));
        }
        return messageMap;
    }
}
