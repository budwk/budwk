package com.budwk.nb.web.commons.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.budwk.nb.sys.models.Sys_user;
import org.nutz.mvc.Mvcs;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/24
 */
public class JwtUtil {

    public static String getUserId() {
        return getUserId(Mvcs.getReq().getHeader("X-Token"));
    }

    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLoginname() {
        return getLoginname(Mvcs.getReq().getHeader("X-Token"));
    }

    public static String getLoginname(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginname").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String sign(Sys_user user, long expireTime) {
        try {
            Date date = new Date(System.currentTimeMillis() + expireTime);
            Algorithm algorithm = Algorithm.HMAC256(user.getPassword());
            return JWT.create()
                    .withClaim("loginname", user.getLoginname())
                    .withClaim("userId", user.getId())
                    .withClaim("uuid", UUID.randomUUID().toString())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static boolean verify(String token, String password) {
        JWTVerifier verifier = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(password);
            verifier = JWT.require(algorithm)
                    .build();
        } catch (Exception e) {
            return false;
        }
        try {
            verifier.verify(token);
        }catch (TokenExpiredException expiredException){
            return false;
        }
        return true;
    }
}
