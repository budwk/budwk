package com.budwk.app.device;

import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init")
@AdaptBy(type = JsonAdaptor.class)
@Slf4j
public class WkDeviceLauncher {
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private Dao dao;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk");
        nb.run();
    }

    public void init() {

    }
}
