package com.budwk.nb.web.commons.core;

import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.ext.i18n.WkLocalizationManager;
import com.budwk.nb.web.commons.ext.pubsub.WebPubSub;
import org.nutz.boot.NbApp;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.ServletContext;

/**
 * @author wizzer(wizzer@qq.com) on 2018/3/16.
 */
@IocBean(create = "init")
@Modules(packages = "com.budwk.nb")
@Localization(value = "locales/", defaultLocalizationKey = "zh_CN")
@Encoding(input = "UTF-8", output = "UTF-8")
@ChainBy(args = "chain/budwk-mvc-chain.json")
@SessionBy(ShiroSessionProvider.class)
public class WebMainLauncher {
    private static final Log log = Logs.get();
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private JedisAgent jedisAgent;
    /**
     * 注入一下为了进行初始化
     */
    @Inject
    private Globals globals;
    /**
     * 注入一下为了进行初始化
     */
    @Inject
    private WebPubSub webPubSub;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }

    public static NbApp warMain(ServletContext sc) {
        NbApp nb = new NbApp().setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        return nb;
    }

    public void init() {
        Mvcs.X_POWERED_BY = "Budwk-V6 <budwk.com>";
        Mvcs.setDefaultLocalizationKey("zh-CN");
        Mvcs.setLocalizationManager(ioc.get(WkLocalizationManager.class));
        Globals.AppBase = Mvcs.getServletContext().getContextPath();
        Globals.AppRoot = Mvcs.getServletContext().getRealPath("/");
    }
}
