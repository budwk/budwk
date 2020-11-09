package com.budwk.nb.app.commons.shiro.realm;

import com.budwk.nb.app.commons.shiro.service.JwtService;
import com.budwk.nb.app.commons.shiro.token.JwtToken;
import com.budwk.nb.shop.commons.utils.JwtUtil;
import com.budwk.nb.sys.models.Sys_user;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/24
 */
@IocBean(name = "jwtRealm")
public class JwtRealm extends AuthorizingRealm {
    @Inject
    private JwtService jwtService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 默认使用此方法进行验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String uid = JwtUtil.getUId(token);
        if (uid == null) {
            throw new AuthenticationException("Token失效,请重新登陆");
        }

        Sys_user sysUser = jwtService.getUser(token);
        if (sysUser == null) {
            throw new AuthenticationException("会员不存在,请重新登陆");
        }

        try {
            if (!JwtUtil.verify(token, sysUser.getPassword())) {
                throw new AuthenticationException("会员密码不正确,请重新登陆");
            }
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}
