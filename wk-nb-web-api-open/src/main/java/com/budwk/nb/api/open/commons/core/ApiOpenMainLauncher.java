package com.budwk.nb.api.open.commons.core;

import com.budwk.nb.api.open.commons.i18n.WkLocalizationManager;
import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.Modules;

/**
 * Created by wizzer on 2018/4/4.
 */
@IocBean(create = "init", depose = "depose")
@Modules(packages = "com.budwk.nb")
public class ApiOpenMainLauncher {
    private static final Log log = Logs.get();
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }

    public void init() {
        Mvcs.X_POWERED_BY = "Budwk-V6 <budwk.com>";
        Mvcs.setDefaultLocalizationKey("zh-CN");
        Mvcs.setLocalizationManager(ioc.get(WkLocalizationManager.class));
    }

    public void depose() {

    }
}
