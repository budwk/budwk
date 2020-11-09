package com.budwk.nb.app.commons.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/16
 */
public class JwtToken implements AuthenticationToken {
    /**
     * 密钥
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}