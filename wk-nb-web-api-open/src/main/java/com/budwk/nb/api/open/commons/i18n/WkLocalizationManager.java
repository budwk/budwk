package com.budwk.nb.api.open.commons.i18n;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.sys.services.SysLangService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.i18n.LocalizationManager;
import org.nutz.mvc.impl.NutMessageMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 多语言字符串
 * Created by wizzer on 2019/10/29
 */
@IocBean
public class WkLocalizationManager implements LocalizationManager {
    @Inject
    @Reference
    private SysLangService sysLangService;

    protected String defaultLocal;

    protected Map<String, NutMessageMap> msgs = new HashMap<String, NutMessageMap>();

    public void setDefaultLocal(String local) {
        this.defaultLocal = local;
    }

    public String getDefaultLocal() {
        return defaultLocal;
    }

    public Set<String> getLocals() {
        return msgs.keySet();
    }

    // 如果要动态替换msg, 例如从数据库读取
    // 请实现一个NutMessageMap的子类, 覆盖其get方法, 替换为动态实现
    public NutMessageMap getMessageMap(String local) {
        return sysLangService.getLang(local);
    }

    public String getMessage(String local, String key) {
        NutMessageMap map = getMessageMap(local);
        if (defaultLocal != null && map == null) {
            map = getMessageMap(defaultLocal);
        }
        if (map == null)
            return key;
        return (String) map.getOrDefault(key, key);
    }
}