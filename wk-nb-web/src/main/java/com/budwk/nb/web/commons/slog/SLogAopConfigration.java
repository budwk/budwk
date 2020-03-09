package com.budwk.nb.web.commons.slog;

import com.budwk.nb.commons.annotation.SLog;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2016/6/22.
 */
@IocBean(name = "$aop_syslog")
public class SLogAopConfigration extends SimpleAopMaker<SLog> {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Override
    public List<? extends MethodInterceptor> makeIt(SLog slog, Method method, Ioc ioc) {
        return Arrays.asList(new SLogAopInterceptor(ioc, slog, method));
    }

    @Override
    public String[] getName() {
        return new String[0];
    }

    @Override
    public boolean has(String name) {
        return false;
    }
}
