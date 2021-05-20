package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.security.IAuthProvider;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = IAuthProvider.class)
@IocBean
public class AuthProvider implements IAuthProvider {
    @Inject
    private SysUserService sysUserService;

    @Override
    public List<String> getPermissionList(String userId) {
        return sysUserService.getPermissionList(userId);
    }

    @Override
    public List<String> getRoleList(String userId) {
        return sysUserService.getRoleList(userId);
    }
}
