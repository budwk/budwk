package com.budwk.nb.app.commons.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.budwk.nb.sys.models.Sys_user;
import org.nutz.mvc.Mvcs;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/16
 */
public class JwtUtil {

    public static String getUId() {
        return getUId(Mvcs.getReq().getHeader("Authorization"));
    }

    public static String getUId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("uid").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLoginname() {
        return getUId(Mvcs.getReq().getHeader("Authorization"));
    }

    public static String getLoginname(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginname").asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成签名
     *
     * @param sysUser 用户
     * @return 加密的token
     */
    public static String sign(Sys_user sysUser, long expireTime) {
        try {
            Date date = new Date(System.currentTimeMillis() + expireTime);
            Algorithm algorithm = Algorithm.HMAC256(sysUser.getPassword());
            return JWT.create()
                    .withClaim("loginname", sysUser.getLoginname())
                    .withClaim("uid", sysUser.getId())
                    .withClaim("uuid", UUID.randomUUID().toString())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token    密钥
     * @param password 用户密码
     * @return 是否正确
     */
    public static boolean verify(String token, String password) {
        JWTVerifier verifier = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(password);
            verifier = JWT.require(algorithm)
                    .build();
        } catch (Exception e) {
            return false;
        }
        DecodedJWT jwt = verifier.verify(token);
        return true;
    }
}
