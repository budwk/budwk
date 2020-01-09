package com.budwk.nb.web.commons.ext.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.wx.models.Wx_config;
import com.budwk.nb.wx.services.WxConfigService;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.at.impl.JedisAgenAccessTokenStore;
import org.nutz.weixin.at.impl.RedisAccessTokenStore;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.spi.WxApi2;
import redis.clients.jedis.JedisPool;

/**
 * @author wizzer(wizzer @ qq.com) on 2018/3/17.
 */
@IocBean
public class WxService {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private WxConfigService wxConfigService;
    @Inject
    private JedisAgent jedisAgent;

    public synchronized WxApi2 getWxApi2(String wxid) {
        WxApi2Impl wxApi2 = Globals.WxMap.getAs(wxid, WxApi2Impl.class);
        if (wxApi2 == null) {
            Wx_config appInfo = wxConfigService.fetch(Cnd.where("id", "=", wxid));
            JedisAgenAccessTokenStore redisAccessTokenStore = new JedisAgenAccessTokenStore(RedisConstant.REDIS_KEY_WX_TOKEN + wxid, jedisAgent);
            wxApi2 = new WxApi2Impl();
            wxApi2.setAppid(appInfo.getAppid());
            wxApi2.setAppsecret(appInfo.getAppsecret());
            wxApi2.setEncodingAesKey(appInfo.getEncodingAESKey());
            wxApi2.setToken(appInfo.getToken());
            wxApi2.setAccessTokenStore(redisAccessTokenStore);
            Globals.WxMap.put(wxid, wxApi2);
        }
        return wxApi2;

    }
}
