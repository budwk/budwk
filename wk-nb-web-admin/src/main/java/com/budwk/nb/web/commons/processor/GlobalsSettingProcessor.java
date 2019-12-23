package com.budwk.nb.web.commons.processor;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * @author wizzer(wizzer@qq.com) on 2016/6/22.
 */
public class GlobalsSettingProcessor extends AbstractProcessor {
    private static final Log log = Logs.get();

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
        doNext(ac);
    }

}
