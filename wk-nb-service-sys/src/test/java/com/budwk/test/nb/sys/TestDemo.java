package com.budwk.test.nb.sys;

import com.budwk.nb.sys.commons.core.SysMainLauncher;
import com.budwk.nb.sys.services.SysConfigService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.boot.NbApp;
import org.nutz.boot.test.junit4.NbJUnit4Runner;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;

/**
 * @author wizzer(wizzer.cn)
 */
@IocBean(create = "init")
@RunWith(NbJUnit4Runner.class)
public class TestDemo extends Assert {
    @Inject
    private SysConfigService sysConfigService;

    public void init() {
        System.out.println("say hi");
    }

    @Test
    public void test_service_inject() {
        assertNotNull(sysConfigService);
        System.out.println("sys_config:::" + Json.toJson(sysConfigService.getAllList()));
    }

    // 测试类可提供public的static的createNbApp方法,用于定制当前测试类所需要的NbApp对象.
    // 测试类带@IocBean或不带@IocBean,本规则一样生效
    // 若不提供,默认使用当前测试类作为MainLauncher.
    // 也可以自定义NbJUnit4Runner, 继承NbJUnit4Runner并覆盖其createNbApp方法
    public static NbApp createNbApp() {
        NbApp nb = new NbApp().setMainClass(SysMainLauncher.class).setPrintProcDoc(false);
        nb.getAppContext().setMainPackage("com.budwk");
        return nb;
    }
}