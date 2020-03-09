package com.budwk.nb.web.commons.ext.i18n;

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
 *
 * @author wizzer(wizzer.cn) on 2019/10/29
 */
@IocBean
public class WkLocalizationManager implements LocalizationManager {
    @Inject
    private SysLangService sysLangService;

    protected String defaultLocal;

    protected Map<String, NutMessageMap> msgs = new HashMap<String, NutMessageMap>();

    @Override
    public void setDefaultLocal(String local) {
        this.defaultLocal = local;
    }

    @Override
    public String getDefaultLocal() {
        return defaultLocal;
    }

    @Override
    public Set<String> getLocals() {
        return msgs.keySet();
    }

    /**
     * 替换为动态实现
     *
     * @param local
     * @return
     */
    @Override
    public NutMessageMap getMessageMap(String local) {
        return sysLangService.getLang(local);
    }

    @Override
    public String getMessage(String local, String key) {
        System.out.println("local:::" + local);
        System.out.println("key:::" + key);
        NutMessageMap map = getMessageMap(local);
        if (defaultLocal != null && map == null) {
            map = getMessageMap(defaultLocal);
        }
        if (map == null) {
            return key;
        }
        return (String) map.getOrDefault(key, key);
    }
}