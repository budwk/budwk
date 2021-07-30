package com.budwk.starter.common.constant;

/**
 * @author wizzer@qq.com
 */
public class RedisConstant {

    public static final String PRE = "wk:";

    /**
     * Token 缓存前缀
     */
    public static final String TOKEN = PRE + "token:";

    /**
     * WorkerId 雪花主键生成器缓存前缀
     */
    public static final String IG_WORKERID = PRE + "ig:workerid:";

    /**
     * wkcache 缓存前缀
     */
    public static final String WKCACHE = PRE + "wkcache:";

    /**
     * wkcache 缓存失效时间(秒)
     */
    public static final int WKCACHE_TIMEOUT = 7200;

    /**
     * 验证码前缀
     */
    public static final String UCENTER_CAPTCHA = PRE + "ucenter:captcha:";

    /**
     * 短信验证码前缀
     */
    public static final String UCENTER_SMSCODE = PRE + "ucenter:smscode:";

    /**
     * 邮件验证码前缀
     */
    public static final String UCENTER_EMAILCODE = PRE + "ucenter:emailcode:";

    /**
     * 平台发布订阅前缀
     */
    public static final String PLATFORM_PUBSUB = PRE + "platform:pubsub:";

    /**
     * 系统参数前缀
     */
    public static final String GLOBALS_DATA = PRE + "globals:data:";

    /**
     * 定时任务发布
     */
    public static final String JOB_PUBLISH = PRE + "job:publish";

    /**
     * 定时任务订阅
     */
    public static final String JOB_SUBSCRIBE = PRE + "job:subscribe";

    /**
     * 定时任务限制实例前缀
     */
    public static final String JOB_EXECUTE = PRE + "job:execute:";

    /**
     * 定时任务限制日志实例前缀
     */
    public static final String JOB_HISTORY = PRE + "job:history:";

    /**
     * websocket 房间前缀
     */
    public static final String WS_ROOM = PRE + "wsroom:";
}
