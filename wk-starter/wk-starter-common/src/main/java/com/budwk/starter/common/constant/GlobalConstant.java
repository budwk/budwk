package com.budwk.starter.common.constant;

public class GlobalConstant {
    /**
     * 软件版本号
     */
    public static final String VERSION = "7.0.0";

    /**
     * 默认时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 微服务之间传递的唯一标识 header
     */
    public static final String WK_TRACE_ID = "wk-trace-id";

    /**
     * MDC日志中名称
     */
    public static final String WK_LOG_TRACE_ID = "TraceId";

    /**
     * header 中租户ID
     */
    public static final String WK_TENANT_ID = "wk-tenant-id";

    /**
     * 租户id参数
     */
    public static final String TENANT_ID_PARAM = "tenantId";

    /**
     * 默认租户ID
     */
    public static final String TENANT_ID_DEFAULT = "platform";

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "v1";

    /**
     * 权限认证的排序
     */
    public static final int FILTER_ORDER_UCENTER = 60;

    /**
     * 签名排序
     */
    public static final int FILTER_ORDER_SIGN = 70;

    /**
     * WEB模块拦截器排序(sql注入及跨站攻击防范)
     */
    public static final int FILTER_ORDER_WEB = 45;

    /**
     * 权限拦截排序
     */
    public static final int FILTER_ORDER_SECURITY = 40;

    /**
     * X_POWER_BY
     */
    public static final String X_POWER_BY = "BUDWK V7 <budwk.com>";

    /**
     * json类型报文
     */
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";

    /**
     * 默认系统管理员角色
     */
    public static final String DEFAULT_SYSADMIN_ROLECODE = "sysadmin";

    /**
     * 默认系统管理员用户名
     */
    public static final String DEFAULT_SYSADMIN_LOGINNAME = "superadmin";

    /**
     * 用户Token参数名
     */
    public static final String HEADER_TOKEN = "X-Token";
    /**
     * 公共应用ID
     */
    public static final String DEFAULT_COMMON_APPID = "COMMON";
    /**
     * 控制中心应用ID
     */
    public static final String DEFAULT_PLATFORM_APPID = "PLATFORM";

}
