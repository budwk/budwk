package com.budwk.nb.api.open.commons.sign;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.SignUtil;
import com.budwk.nb.sys.services.SysApiService;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Map;

/**
 * 签名验证服务
 * @author wizzer(wizzer@qq.com) on 2019/10/28
 */
@IocBean
public class ApiSignServer {
    private static final Log log = Logs.get();
    @Inject
    private PropertiesProxy conf;
    @Inject
    @Reference
    private SysApiService sysApiService;
    @Inject
    private RedisService redisService;
    /**
     * 签名验证有效时间：一分钟
     */
    private static final int TIMEOUT_ONE_MINIUTE = 60000;

    public Result checkSign(Map<String, Object> paramMap) {
        try {
            String appid = Strings.sNull(paramMap.get("appid"));
            String sign = Strings.sNull(paramMap.get("sign"));
            String timestamp = Strings.sNull(paramMap.get("timestamp"));
            String nonce = Strings.sNull(paramMap.get("nonce"));
            String appkey = sysApiService.getAppkey(appid);
            if (Strings.isBlank(appid) || Strings.isBlank(appkey)) {
                return Result.error(500301, "system.api.error.appid");
            }
            // 时间戳相差大于1分钟则为无效的
            if (Times.getTS() - Long.parseLong(timestamp) > TIMEOUT_ONE_MINIUTE) {
                return Result.error(500302, "system.api.error.timestamp");
            }
            if (Strings.isBlank(nonce)) {
                return Result.error(500303, "system.api.error.nonce");
            }
            String nonceCache = redisService.get(RedisConstant.REDIS_KEY_API_SIGN_OPEN_NONCE + appid + "_" + nonce);
            // 如果一分钟内nonce是重复的则为无效,让nonce只能使用一次
            if (Strings.isNotBlank(nonceCache)) {
                return Result.error(500303, "system.api.error.nonce");
            }
            if (SignUtil.createSign(appkey, paramMap).equalsIgnoreCase(sign)) {
                return Result.error(500304, "system.api.error.sigin");
            }
            //nonce保存到缓存
            redisService.setex(RedisConstant.REDIS_KEY_API_SIGN_OPEN_NONCE + appid + "_" + nonce, 60, nonce);
            return Result.success("system.api.sign.success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500300, "system.api.error");
        }
    }
}
