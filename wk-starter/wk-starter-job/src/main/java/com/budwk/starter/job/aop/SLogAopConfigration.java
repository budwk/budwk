package com.budwk.starter.job.aop;

import com.budwk.starter.job.annotation.SJob;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(name = "$aop_wksjob")
public class SLogAopConfigration extends SimpleAopMaker<SJob> {
    public List<? extends MethodInterceptor> makeIt(SJob sJob, Method method, Ioc ioc) {
        return Arrays.asList(ioc.get(SJobAopInterceptor.class));
    }

    public String[] getName() {
        return new String[0];
    }

    public boolean has(String name) {
        return false;
    }
}
