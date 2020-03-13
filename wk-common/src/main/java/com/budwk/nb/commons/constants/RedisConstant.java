package com.budwk.nb.commons.constants;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/12/13
 */
public class RedisConstant {
    public final static String PLATFORM_REDIS_PREFIX = "budwk:";
    public final static String PLATFORM_REDIS_WKCACHE_PREFIX = PLATFORM_REDIS_PREFIX + "wkcache:";
    public final static String REDIS_KEY_WSROOM = PLATFORM_REDIS_PREFIX + "wsroom:";
    public final static String REDIS_KEY_APP_DEPLOY = PLATFORM_REDIS_PREFIX + "deploy:";
    public final static String REDIS_KEY_API_SIGN_DEPLOY_NONCE = PLATFORM_REDIS_PREFIX + "api:sign:deploy:nonce:";
    public final static String REDIS_KEY_API_SIGN_OPEN_NONCE = PLATFORM_REDIS_PREFIX + "api:sign:open:nonce:";
    public final static String REDIS_KEY_API_TOKEN_NONCE = PLATFORM_REDIS_PREFIX + "api:token:";
    public final static String REDIS_KEY_LOGBACK_LOGLEVEL_LIST = PLATFORM_REDIS_PREFIX + "logback:loglevel:list:";
    public final static String REDIS_KEY_LOGIN_ADMIN_CAPTCHA = PLATFORM_REDIS_PREFIX + "admin:login:captcha:";
    public final static String REDIS_KEY_LOGIN_ADMIN_SESSION = PLATFORM_REDIS_PREFIX + "admin:login:session:";
    public final static String REDIS_KEY_ADMIN_PUBSUB = PLATFORM_REDIS_PREFIX + "admin:pubsub:";
    public final static String REDIS_KEY_WX_TOKEN = PLATFORM_REDIS_PREFIX + "wx:token:";
}
