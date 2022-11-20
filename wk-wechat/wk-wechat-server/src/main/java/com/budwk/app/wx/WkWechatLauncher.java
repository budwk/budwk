package com.budwk.app.wx;

import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;

/**
 * @author wizzer@qq.com
 */
@IocBean
@AdaptBy(type = JsonAdaptor.class)
@Slf4j
public class WkWechatLauncher {

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk");
        nb.run();
    }
}
