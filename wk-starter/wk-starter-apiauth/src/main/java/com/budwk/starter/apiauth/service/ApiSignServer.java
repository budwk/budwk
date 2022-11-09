package com.budwk.starter.apiauth.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.starter.apiauth.providers.IApiAuthProvider;
import com.budwk.starter.apiauth.storage.CacheStorage;
import com.budwk.starter.apiauth.util.SignUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.Map;

/**
 * 签名验证服务
 *
 * @author wizzer(wizzer @ qq.com) on 2019/10/28
 */
@IocBean
@Slf4j
public class ApiSignServer {
    private static final String REDIS_KEY_API_SIGN_OPEN_NONCE = "api:auth:nonce:";
    private static final String CONFIG_KEY_API_APPKEY = "api.auth.key";

    private static final String IS_DEBUG = "api.auth.debug";
    @Inject
    private PropertiesProxy conf;
    @Inject
    private CacheStorage cacheStorage;
    @Inject
    @Reference(check = false)
    private IApiAuthProvider apiAuthProvider;
    /**
     * 签名验证有效时间：一分钟
     */
    private static final int TIMEOUT_ONE_MINIUTE = 60000;

    public Result<?> checkSign(Map<String, Object> paramMap) {
        try {
            if (conf.getBoolean(IS_DEBUG, false)) {
                log.warn("[Check Sign] 请注意！当前是DEBUG模式，不进行签名校验！");
                return Result.success();
            }
            String appid = Strings.sNull(paramMap.get("appid"));
            String sign = Strings.sNull(paramMap.get("sign"));
            String timestamp = Strings.sNull(paramMap.get("timestamp"));
            String nonce = Strings.sNull(paramMap.get("nonce"));
            String appkey = getAppKey(appid);
            if (Strings.isBlank(appid) || Strings.isBlank(appkey)) {
                return Result.error(ResultCode.APPID_ERROR);
            }
            // 时间戳相差大于1分钟则为无效的
            if (Times.getTS() - Long.parseLong(timestamp) > TIMEOUT_ONE_MINIUTE) {
                return Result.error(ResultCode.TIMESTAMP_ERROR);
            }
            if (Strings.isBlank(nonce)) {
                return Result.error(ResultCode.NONCE_ERROR);
            }
            String nonceCache = cacheStorage.get(REDIS_KEY_API_SIGN_OPEN_NONCE + appid + "_" + nonce);
            // 如果一分钟内nonce是重复的则为无效,让nonce只能使用一次
            if (Strings.isNotBlank(nonceCache)) {
                return Result.error(ResultCode.NONCE_DUPLICATION);
            }
            if (!SignUtil.createSign(appkey, paramMap).equalsIgnoreCase(sign)) {
                return Result.error(ResultCode.SIGIN_ERROR);
            }
            //nonce保存到缓存
            cacheStorage.set(REDIS_KEY_API_SIGN_OPEN_NONCE + appid + "_" + nonce, nonce, 60L);
            return Result.success();
        } catch (Exception e) {
            log.error("签名校验出错", e);
            return Result.error(ResultCode.FAILURE, "签名校验出错");
        }
    }

    private String getAppKey(String appId) {
        if (null != apiAuthProvider) {
            return apiAuthProvider.getAppKey(appId);
        }
        return conf.get("api.auth.key." + appId);
    }
}
