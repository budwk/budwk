package com.budwk.starter.log.aop;

import com.budwk.starter.log.annotation.SLog;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@IocBean(name = "$aop_wkslog")
public class SLogAopConfigration extends SimpleAopMaker<SLog> {
	
	public List<? extends MethodInterceptor> makeIt(SLog slog, Method method, Ioc ioc) {
		return Arrays.asList(ioc.get(SLogAopInterceptor.class));
	}

	public String[] getName() {
		return new String[0];
	}
	
	public boolean has(String name) {
		return false;
	}
}
