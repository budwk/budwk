package com.budwk.app.device.handler;

import com.budwk.app.device.handler.container.DeviceHandlerContainer;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init",depose = "close")
public class DeviceHandlerLauncher {

    @Inject
    private DeviceHandlerContainer container;

    public static void main(String[] args) {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk");
        nb.run();
    }


    public void init() throws Exception {
        //container.start();
    }

    public void close() throws Exception {
        //container.stop();
    }
}
