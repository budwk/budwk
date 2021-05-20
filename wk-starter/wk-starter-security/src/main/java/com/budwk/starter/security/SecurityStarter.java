package com.budwk.starter.security;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.security.satoken.SaTokenContextImpl;
import com.budwk.starter.security.satoken.SaTokenDaoRedisImpl;
import com.budwk.starter.security.satoken.StpInterfaceImpl;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class SecurityStarter {
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected static final String PRE = "security.";

    @PropDoc(value = "Token 名称", defaultValue = GlobalConstant.HEADER_TOKEN)
    public static final String PROP_SECURITY_ST_TOKENNAME = PRE + "st.tokenName";

    @PropDoc(value = "Token 超时时间(单位s,默认1天)", defaultValue = "86400", type = "long")
    public static final String PROP_SECURITY_ST_TIMEOUT = PRE + "st.timeout";

    @PropDoc(value = "Token 是否允许同一账号并发登录(为false时新登录挤掉旧登录)", defaultValue = "true", type = "boolean")
    public static final String PROP_SECURITY_ST_ALLOW_CONCURRENTLOGIN = PRE + "st.allowConcurrentLogin";

    @PropDoc(value = "Token 在多人登录同一账号时,是否共用一个token(为false时每次登录新建一个token)", defaultValue = "true", type = "boolean")
    public static final String PROP_SECURITY_ST_ISSHARE = PRE + "st.isShare";

    @PropDoc(value = "Token 风格(默认可取值：uuid,simple-uuid,random-32,random-64,random-128,tik)", defaultValue = "uuid")
    public static final String PROP_SECURITY_ST_TOKENSTYLE = PRE + "st.tokenStyle";

    @PropDoc(value = "Token 是否打开自动续签", defaultValue = "true", type = "boolean")
    public static final String PROP_SECURITY_ST_AUTORENEW = PRE + "st.autoRenew";

    @PropDoc(value = "Token 写入Cookie时显式指定的作用域,常用于单点登录二级域名共享Cookie的场景", defaultValue = "")
    public static final String PROP_SECURITY_ST_COOKIEDOMAIN = PRE + "st.cookieDomain";

//    @PropDoc(value = "Token 持久化方式(api=通过API,redis=通过redis)", defaultValue = "redis")
//    public static final String PROP_SECURITY_MODE = PRE + "mode";
//
//    @PropDoc(value = "Token 持久化API方式认证中心地址", defaultValue = "http://127.0.0.1:9120")
//    public static final String PROP_SECURITY_HOST = PRE + "host";

    public void init() {
        SaTokenConfig saTokenConfig = conf.makeDeep(SaTokenConfig.class, PRE + "st.");
        String tokenName = conf.get(PROP_SECURITY_ST_TOKENNAME, GlobalConstant.HEADER_TOKEN);
        // 注意这里的token默认超时时间与控制中心 websocket 超时时间保持一致
        saTokenConfig.setTimeout(conf.getLong(PROP_SECURITY_ST_TIMEOUT,86400));
        saTokenConfig.setTokenName(tokenName);
        saTokenConfig.setIsV(false);
        SaManager.setConfig(saTokenConfig);
        SaManager.setSaTokenContext(ioc.get(SaTokenContextImpl.class));
        SaManager.setSaTokenDao(ioc.get(SaTokenDaoRedisImpl.class));
        SaManager.setStpInterface(ioc.get(StpInterfaceImpl.class));
    }

}
