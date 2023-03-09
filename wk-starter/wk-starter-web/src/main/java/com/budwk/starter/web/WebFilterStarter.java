package com.budwk.starter.web;

import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.web.filter.WebFilter;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class WebFilterStarter implements WebFilterFace {
    private static final Log log = Logs.get();

    protected static final String PRE = "web.";

    @PropDoc(value = "web 是否启用跨站攻击拦截", defaultValue = "true", type = "boolean")
    public static final String PROP_WEB_XSS_ENABLE = PRE + "xss.enable";

    @PropDoc(value = "web 是否启用SQL注入拦截", defaultValue = "true", type = "boolean")
    public static final String PROP_WEB_SQL_ENABLE = PRE + "sql.enable";

    @PropDoc(value = "web SQL注入忽略的URLS", defaultValue = "")
    public static final String PROP_WEB_SQL_IGNORE = PRE + "sql.ignore";

    @PropDoc(value = "web 是否启用跨域访问", defaultValue = "true", type = "boolean")
    public static final String PROP_WEB_CORS_ENABLE = PRE + "cors.enable";

    @PropDoc(value = "web 跨站允许域名", defaultValue = "*")
    public static final String PROP_WEB_CORS_ORIGIN = PRE + "cors.origin";

    @PropDoc(value = "web 跨站有效期", defaultValue = "1800", type = "integer")
    public static final String PROP_WEB_CORS_MAXAGE = PRE + "cors.maxage";

    @PropDoc(value = "web 跨站请求方法", defaultValue = "GET,POST,PUT,DELETE")
    public static final String PROP_WEB_CORS_METHODS = PRE + "cors.methods";

    @PropDoc(value = "web 跨站请求域参数名", defaultValue = "X-Requested-With,Content-Type,lang,wk-member-token,wk-user-token")
    public static final String PROP_WEB_CORS_HEADERS = PRE + "cors.headers";

    private boolean xssEnable;
    private boolean sqlEnable;
    private List<String> sqlIgnoreUrls;
    private boolean corsEnable;
    private String corsOrigin;
    private int corsMaxAge;
    private String corsMethods;
    private String corsHeaders;

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        // getOrder = 40 ,在 NutFilter 之前,才能设置自定义动作链文件
        conf.set("web.filter.chain.path", "chain/chain.json");
        // 设置默认忽略文件
        conf.set("nutz.mvc.ignore",conf.get("nutz.mvc.ignore","^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|ico|map|mp3|mp4|apk)$"));
        // 设置排除路径
        conf.set("nutz.mvc.exclusions",conf.get("nutz.mvc.exclusions","/druid/*,/upload/*,/openapi/*"));

        xssEnable = conf.getBoolean(PROP_WEB_XSS_ENABLE, true);
        sqlEnable = conf.getBoolean(PROP_WEB_SQL_ENABLE, true);
        sqlIgnoreUrls = conf.getList(PROP_WEB_SQL_IGNORE);
        corsEnable = conf.getBoolean(PROP_WEB_CORS_ENABLE, true);
        corsOrigin = conf.get(PROP_WEB_CORS_ORIGIN, "*");
        corsMaxAge = conf.getInt(PROP_WEB_CORS_MAXAGE, 1800);
        corsMethods = conf.get(PROP_WEB_CORS_METHODS, "GET,POST,PUT,DELETE");
        corsHeaders = conf.get(PROP_WEB_CORS_HEADERS, "X-Requested-With,Content-Type,lang,appId,wk-member-token,wk-user-token");
        Mvcs.X_POWERED_BY = GlobalConstant.X_POWER_BY;
    }

    @Override
    public String getName() {
        return "web";
    }

    @Override
    public String getPathSpec() {
        return "/*";
    }

    @Override
    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    @IocBean(name = "wkWebFilter")
    public WebFilter createWebFilter() {
        return new WebFilter(xssEnable, sqlEnable, sqlIgnoreUrls, corsEnable, corsOrigin, corsMaxAge, corsMethods, corsHeaders);
    }

    @Override
    public Filter getFilter() {
        return ioc.get(WebFilter.class, "wkWebFilter");
    }

    @Override
    public Map<String, String> getInitParameters() {
        return new HashMap<String, String>();
    }

    @Override
    public int getOrder() {
        return GlobalConstant.FILTER_ORDER_WEB;
    }
}
