package com.budwk.nb.api.open.commons.token;

import com.budwk.nb.commons.constants.RedisConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.io.*;
import java.security.Key;
import java.util.Date;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
@IocBean
public class ApiTokenServer {
    @Inject
    private RedisService redisService;

    /**
     * 获取KEY
     *
     * @param appid appid
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Key getKey(String appid) throws IOException, ClassNotFoundException {
        Key key;
        byte[] obj = redisService.get((RedisConstant.REDIS_KEY_API_TOKEN_NONCE + appid).getBytes());
        if (obj != null) {
            ObjectInputStream keyIn = new ObjectInputStream(new ByteArrayInputStream(obj));
            key = (Key) keyIn.readObject();
            keyIn.close();
        } else {
            key = MacProvider.generateKey();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(key);
            obj = bao.toByteArray();
            // 2小时零2秒后自动删除
            redisService.setex((RedisConstant.REDIS_KEY_API_TOKEN_NONCE + appid).getBytes(), 7202, obj);
        }
        return key;
    }

    /**
     * 生成token
     *
     * @param date  失效时间
     * @param appid appid
     * @return
     */
    public String generateToken(Date date, String appid) throws IOException, ClassNotFoundException {
        return Jwts.builder()
                .setSubject(appid)
                .signWith(getKey(appid))
                .setExpiration(date)
                .compact();
    }

    /**
     * 验证token
     *
     * @param appid appid
     * @param token token
     * @return
     */
    public boolean verifyToken(String appid, String token) {
        try {
            return Jwts.parser().setSigningKey(getKey(appid)).parseClaimsJws(token).getBody().getSubject().equals(appid);
        } catch (Exception e) {
            return false;
        }
    }
}
