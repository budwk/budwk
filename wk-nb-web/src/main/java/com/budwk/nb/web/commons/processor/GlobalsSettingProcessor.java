package com.budwk.nb.web.commons.processor;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * @author wizzer(wizzer.cn) on 2016/6/22.
 */
public class GlobalsSettingProcessor extends AbstractProcessor {
    private static final Log log = Logs.get();
    private PropertiesProxy conf;
    private boolean ACCESS_ALLOW_ORIGIN_ENABLED;
    private String ACCESS_ALLOW_ORIGIN_MAXAGE;
    private String ACCESS_ALLOW_ORIGIN_DOMAIN;

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        conf = config.getIoc().get(PropertiesProxy.class, "conf");
        ACCESS_ALLOW_ORIGIN_ENABLED = conf.getBoolean("budwk.access.allow.origin.enabled", false);
        ACCESS_ALLOW_ORIGIN_DOMAIN = conf.get("budwk.access.allow.origin.domain", "127.0.0.1");
        ACCESS_ALLOW_ORIGIN_MAXAGE = conf.get("budwk.access.allow.origin.maxage", "0");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void process(ActionContext ac) throws Throwable {
        // 如果url中有语言属性则设置
        String lang = ac.getRequest().getHeader("X-Lang");
        if (!Strings.isEmpty(lang)) {
            Mvcs.setLocalizationKey(lang);
        } else {
            if (Strings.isBlank(Mvcs.getLocalizationKey())) {
                Mvcs.setLocalizationKey(Mvcs.getDefaultLocalizationKey());
            }
            lang = Mvcs.getLocalizationKey();
        }
        ac.getRequest().setAttribute("lang", lang);
        if (ACCESS_ALLOW_ORIGIN_ENABLED) {
            ac.getResponse().addHeader("Access-Control-Allow-Origin", ACCESS_ALLOW_ORIGIN_DOMAIN);
            ac.getResponse().addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            ac.getResponse().addHeader("Access-Control-Allow-Headers",
                    "X-Requested-With,X-Token");
            ac.getResponse().addHeader("Access-Control-Max-Age", ACCESS_ALLOW_ORIGIN_MAXAGE);
        }
        doNext(ac);
    }

}
