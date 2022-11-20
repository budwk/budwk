package com.budwk.app.wx.commons.service;

import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.at.impl.JedisAgenAccessTokenStore;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.spi.WxApi2;

/**
 * @author wizzer(wizzer.cn) on 2018/3/17.
 */
@IocBean
public class WxService implements PubSub {
    private static final Log log = Logs.get();
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private JedisAgent jedisAgent;
    private NutMap WxMap = NutMap.NEW();

    public synchronized WxApi2 getWxApi2(String wxid) {
        WxApi2Impl wxApi2 = WxMap.getAs(wxid, WxApi2Impl.class);
        if (wxApi2 == null) {
            Wx_config appInfo = wxConfigService.fetch(Cnd.where("id", "=", wxid));
            JedisAgenAccessTokenStore redisAccessTokenStore = new JedisAgenAccessTokenStore(RedisConstant.PRE + ":wxtoken:" + wxid, jedisAgent);
            wxApi2 = new WxApi2Impl();
            wxApi2.setAppid(appInfo.getAppid());
            wxApi2.setAppsecret(appInfo.getAppsecret());
            wxApi2.setEncodingAesKey(appInfo.getEncodingAESKey());
            wxApi2.setToken(appInfo.getToken());
            wxApi2.setAccessTokenStore(redisAccessTokenStore);
            WxMap.put(wxid, wxApi2);
        }
        return wxApi2;
    }

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equals(RedisConstant.PRE + ":wxpubsub")){
            WxMap.clear();
        }
    }
}
