package com.budwk.starter.security.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.starter.security.IAuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    @Reference(check = false)
    private IAuthProvider iAuthProvider;
    @Inject("refer:$ioc")
    protected Ioc ioc;

    public IAuthProvider getIAuthProvider() {
        if (iAuthProvider == null) {
            return ioc.getByType(IAuthProvider.class);
        }
        return iAuthProvider;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        return getIAuthProvider().getPermissionList(Strings.sNull(loginId));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        return getIAuthProvider().getRoleList(Strings.sNull(loginId));
    }
}
