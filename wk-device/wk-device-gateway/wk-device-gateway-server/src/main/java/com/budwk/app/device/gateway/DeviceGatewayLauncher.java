package com.budwk.app.device.gateway;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean
public class DeviceGatewayLauncher {
    public static void main(String[] args) {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk");
        nb.run();
    }
}
