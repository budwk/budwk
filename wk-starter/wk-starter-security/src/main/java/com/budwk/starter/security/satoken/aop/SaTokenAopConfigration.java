package com.budwk.starter.security.satoken.aop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(name = "$aop_satoken")
public class SaTokenAopConfigration implements AopConfigration {

    @Override
    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
        List<InterceptorPair> list = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            SaCheckLogin saCheckLogin = method.getAnnotation(SaCheckLogin.class);
            if (saCheckLogin != null) {
                SaCheckLoginInterceptor saCheckLoginInterceptor = ioc.get(SaCheckLoginInterceptor.class);
                saCheckLoginInterceptor.prepare(saCheckLogin);
                list.add(new InterceptorPair(saCheckLoginInterceptor, new SaTokenMethodMatcher(method)));
            }
            SaCheckRole saCheckRole = method.getAnnotation(SaCheckRole.class);
            if (saCheckRole != null) {
                SaCheckRoleInterceptor saCheckRoleInterceptor = ioc.get(SaCheckRoleInterceptor.class);
                saCheckRoleInterceptor.prepare(saCheckRole);
                list.add(new InterceptorPair(saCheckRoleInterceptor, new SaTokenMethodMatcher(method)));
            }
            SaCheckPermission saCheckPermission = method.getAnnotation(SaCheckPermission.class);
            if (saCheckPermission != null) {
                SaCheckPermissionInterceptor saCheckPermissionInterceptor = ioc.get(SaCheckPermissionInterceptor.class);
                saCheckPermissionInterceptor.prepare(saCheckPermission);
                list.add(new InterceptorPair(saCheckPermissionInterceptor, new SaTokenMethodMatcher(method)));
            }
        }
        if (list.size() == 0)
            return null;
        return list;
    }
}
