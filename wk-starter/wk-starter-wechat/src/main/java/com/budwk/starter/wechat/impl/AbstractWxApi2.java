package com.budwk.starter.wechat.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import com.budwk.starter.wechat.WxException;
import com.budwk.starter.wechat.at.WxAccessToken;
import com.budwk.starter.wechat.at.WxCardTicket;
import com.budwk.starter.wechat.at.WxJsapiTicket;
import com.budwk.starter.wechat.at.impl.MemoryAccessTokenStore;
import com.budwk.starter.wechat.at.impl.MemoryCardTicketStore;
import com.budwk.starter.wechat.at.impl.MemoryJsapiTicketStore;
import com.budwk.starter.wechat.bean.WxInMsg;
import com.budwk.starter.wechat.bean.WxOutMsg;
import com.budwk.starter.wechat.repo.com.qq.weixin.mp.aes.AesException;
import com.budwk.starter.wechat.repo.com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.budwk.starter.wechat.spi.WxAccessTokenStore;
import com.budwk.starter.wechat.spi.WxApi2;
import com.budwk.starter.wechat.spi.WxCardTicketStore;
import com.budwk.starter.wechat.spi.WxHandler;
import com.budwk.starter.wechat.spi.WxJsapiTicketStore;
import com.budwk.starter.wechat.spi.WxResp;
import com.budwk.starter.wechat.util.BeanConfigures;
import com.budwk.starter.wechat.util.Wxs;

public abstract class AbstractWxApi2 implements WxApi2 {

    private static final Log log = Logs.get();

    protected String token;
    protected String appid;
    protected String appsecret;
    protected String wxBase = "https://api.weixin.qq.com";
    protected String base = wxBase + "/cgi-bin";
    protected String mpBase = "https://mp.weixin.qq.com";
    protected String payBase = "https://api.mch.weixin.qq.com";
    protected String openid;
    protected String encodingAesKey;
    protected int retryTimes = 3;//默认access_token时效时重试次数

    protected PropertiesProxy conf;
    protected String confKeyPrefix = "weixin.";

    public AbstractWxApi2(String token, String appid, String appsecret, String openid, String encodingAesKey) {
        this();
        this.token = token;
        this.appid = appid;
        this.appsecret = appsecret;
        this.openid = openid;
        this.encodingAesKey = encodingAesKey;
    }

    public void init() {
        this.configure(conf, confKeyPrefix);
    }

    public WxApi2 configure(PropertiesProxy conf, String prefix) {
        prefix = Strings.sBlank(prefix);
        token = conf.check(prefix + "token");
        appid = conf.get(prefix + "appid");
        appsecret = conf.get(prefix + "appsecret");
        openid = conf.get(prefix + "openid");
        encodingAesKey = conf.get(prefix + "aes");
        return this;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the appid
     */
    public String getAppid() {
        return appid;
    }

    /**
     * @param appid the appid to set
     */
    public void setAppid(String appid) {
        this.appid = appid;
    }

    /**
     * @return the appsecret
     */
    public String getAppsecret() {
        return appsecret;
    }

    /**
     * @param appsecret the appsecret to set
     */
    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    /**
     * @return the openid
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * @param openid the openid to set
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /**
     * @return the encodingAesKey
     */
    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    /**
     * @param encodingAesKey the encodingAesKey to set
     */
    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    protected Object lock = new Object();

    protected WXBizMsgCrypt pc;

    protected WxAccessTokenStore accessTokenStore;

    protected WxJsapiTicketStore jsapiTicketStore;

    protected WxCardTicketStore cardTicketStore;

    public AbstractWxApi2() {
        this.accessTokenStore = new MemoryAccessTokenStore();
        this.jsapiTicketStore = new MemoryJsapiTicketStore();
        this.cardTicketStore = new MemoryCardTicketStore();
    }

    @Override
    public WxAccessTokenStore getAccessTokenStore() {
        return accessTokenStore;
    }

    @Override
    public void setAccessTokenStore(WxAccessTokenStore ats) {
        this.accessTokenStore = ats;
    }

    @Override
    public WxJsapiTicketStore getJsapiTicketStore() {
        return jsapiTicketStore;
    }

    @Override
    public void setJsapiTicketStore(WxJsapiTicketStore jsapiTicketStore) {
        this.jsapiTicketStore = jsapiTicketStore;
    }

    @Override
    public WxCardTicketStore getCardTicketStore() {
        return cardTicketStore;
    }

    @Override
    public void setCardTicketStore(WxCardTicketStore cardTicketStore) {
        this.cardTicketStore = cardTicketStore;
    }

    protected synchronized void checkWXBizMsgCrypt() {
        if (pc != null || encodingAesKey == null || token == null || appid == null)
            return;
        try {
            pc = new WXBizMsgCrypt(token, encodingAesKey, appid);
        } catch (AesException e) {
            throw new WxException(e);
        }
    }

    @Override
    public WxInMsg parse(HttpServletRequest req) {
        InputStream in;
        try {
            in = req.getInputStream();
        } catch (IOException e) {
            throw new WxException(e);
        }
        String encrypt_type = req.getParameter("encrypt_type");
        if (encrypt_type == null || "raw".equals(encrypt_type))
            return Wxs.convert(in);
        checkWXBizMsgCrypt();
        if (pc == null)
            throw new WxException("encrypt message, but not configure token/encodingAesKey/appid");
        try {
            String msg_signature = req.getParameter("msg_signature");
            String timestamp = req.getParameter("timestamp");
            String nonce = req.getParameter("nonce");
            String str = pc.decryptMsg(msg_signature, timestamp, nonce,
                    new String(Streams.readBytesAndClose(in), Encoding.CHARSET_UTF8));
            return Wxs.convert(str);
        } catch (AesException e) {
            throw new WxException("bad message or bad encodingAesKey", e);
        }
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, WxHandler handler) {
        try {
            WxInMsg in = parse(req);
            WxOutMsg out = handler.handle(in);
            StringWriter sw = new StringWriter();
            Wxs.asXml(sw, out);
            String re = sw.getBuffer().toString();
            if (pc != null)
                re = pc.encryptMsg(re, req.getParameter("timestamp"), req.getParameter("nonce"));
            resp.getWriter().write(re);
        } catch (AesException e) {
            throw new WxException(e);
        } catch (IOException e) {
            throw new WxException(e);
        }
    }

    protected WxResp get(String uri, String... args) {
        String params = "";
        for (int i = 0; i < args.length; i += 2) {
            if (args[i + 1] != null)
                params += "&" + args[i] + "=" + args[i + 1];
        }
        return call(uri + "?_=1&" + params, METHOD.GET, null);
    }

    protected WxResp postJson(String uri, Object... args) {
        NutMap body = new NutMap();
        for (int i = 0; i < args.length; i += 2) {
            body.put(args[i].toString(), args[i + 1]);
        }
        return postJson(uri, body);
    }

    protected WxResp postJson(String uri, NutMap body) {
        return call(uri, METHOD.POST, Json.toJson(body));
    }

    protected WxResp call(String URL, METHOD method, String body) {
        if (log.isInfoEnabled()) {
            log.info("wxapi call: " + URL);
            if (log.isDebugEnabled()) {
                log.debug(body);
            }
        }

        int retry = retryTimes;
        WxResp wxResp = null;
        while (retry >= 0) {
            try {
                String token = getAccessToken();
                String sendUrl = URL.startsWith("http") ? URL : base + URL;
                if (URL.contains("?")) {
                    sendUrl += "&access_token=" + token;
                } else {
                    sendUrl += "?access_token=" + token;
                }
                Request req = Request.create(sendUrl, method);
                if (body != null)
                    req.setData(body);
                Response resp = Sender.create(req).send();
                if (!resp.isOK())
                    throw new IllegalArgumentException("resp code=" + resp.getStatus());
                wxResp = Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
                // 处理微信返回  40001 invalid credential
                if (wxResp.errcode() != 40001) {
                    break;//正常直接返回
                } else {
                    log.warnf("wxapi of access_token request [%s] finished, but the return code is 40001, try to reflush access_token right now, surplus retry times : %s", URL, retry);
                    // 强制刷新一次acess_token
                    reflushAccessToken();
                }
            } catch (Exception e) {
                if (retry >= 0) {
                    log.warn("reflushing access_token... " + retry + " retries left.", e);
                } else {
                    log.errorf("%s times attempts to get a wx access_token , but all failed!", retryTimes);
                    throw Lang.wrapThrow(e);
                }
            } finally {
                retry--;
            }
        }
        return wxResp;
    }

    @Override
    public String getJsapiTicket() {
        WxJsapiTicket at = jsapiTicketStore.get();
        if (at == null || at.getExpires() < (System.currentTimeMillis() - at.getLastCacheTimeMillis()) / 1000) {
            synchronized (lock) {
                WxJsapiTicket at_forupdate = jsapiTicketStore.get();
                if (at_forupdate == null || at_forupdate.getExpires() < (System.currentTimeMillis() - at_forupdate.getLastCacheTimeMillis()) / 1000) {
                    reflushJsapiTicket();
                }
            }
        }
        return jsapiTicketStore.get().getTicket();
    }

    protected void reflushJsapiTicket() {
        String at = this.getAccessToken();
        String url = String.format("%s/ticket/getticket?access_token=%s&type=jsapi", base, at);
        if (log.isDebugEnabled())
            log.debugf("ATS: reflush jsapi ticket send: %s", url);

        Response resp = Http.get(url);
        if (!resp.isOK())
            throw new IllegalArgumentException("reflushJsapiTicket FAIL , openid=" + openid);
        String str = resp.getContent();

        if (log.isDebugEnabled())
            log.debugf("ATS: reflush jsapi ticket done: %s", str);

        NutMap re = Json.fromJson(NutMap.class, str);
        String ticket = re.getString("ticket");
        int expires = re.getInt("expires_in") - 200;//微信默认超时为7200秒，此处设置稍微短一点
        jsapiTicketStore.save(ticket, expires, System.currentTimeMillis());
    }

    @Override
    public String getCardTicket() {
        WxCardTicket ct = cardTicketStore.get();
        if (ct == null || ct.getExpires() < (System.currentTimeMillis() - ct.getLastCacheTimeMillis()) / 1000) {
            synchronized (lock) {
            	WxCardTicket ct_forupdate = cardTicketStore.get();
                if (ct_forupdate == null || ct_forupdate.getExpires() < (System.currentTimeMillis() - ct_forupdate.getLastCacheTimeMillis()) / 1000) {
                    reflushCardTicket();
                }
            }
        }
        return cardTicketStore.get().getTicket();
    }

    protected void reflushCardTicket() {
        String at = this.getAccessToken();
        String url = String.format("%s/ticket/getticket?access_token=%s&type=wx_card", base, at);
        if (log.isDebugEnabled()) {
        	log.debugf("ATS: reflush wx_card ticket send: %s", url);
        }

        Response resp = Http.get(url);
        if (!resp.isOK()) {
            throw new IllegalArgumentException("reflushCardTicket FAIL , openid=" + openid);
        }
        String str = resp.getContent();

        if (log.isDebugEnabled()) {
            log.debugf("ATS: reflush wx_card ticket done: %s", str);
        }

        NutMap re = Json.fromJson(NutMap.class, str);
        String ticket = re.getString("ticket");
        int expires = re.getInt("expires_in") - 200;//微信默认超时为7200秒，此处设置稍微短一点
        cardTicketStore.save(ticket, expires, System.currentTimeMillis());
    }

    @Override
    public String getAccessToken() {
        WxAccessToken at = accessTokenStore.get();
        if (at == null || at.getExpires() < (System.currentTimeMillis() - at.getLastCacheTimeMillis()) / 1000) {
            synchronized (lock) {
                //FIX多线程更新token的问题
                WxAccessToken at_forupdate = accessTokenStore.get();
                if (at_forupdate == null || at_forupdate.getExpires() < (System.currentTimeMillis() - at_forupdate.getLastCacheTimeMillis()) / 1000) {
                    reflushAccessToken();
                }
            }
        }
        return accessTokenStore.get().getToken();
    }

    protected synchronized void reflushAccessToken() {
        String url = String.format("%s/token?grant_type=client_credential&appid=%s&secret=%s", base, appid, appsecret);
        if (log.isDebugEnabled())
            log.debugf("ATS: reflush access_token send: %s", url);

        Response resp = Http.get(url);
        if (!resp.isOK())
            throw new IllegalArgumentException("reflushAccessToken FAIL , openid=" + openid);
        String str = resp.getContent();

        if (log.isDebugEnabled())
            log.debugf("ATS: reflush access_token done: %s", str);

        NutMap re = Json.fromJson(NutMap.class, str);
		if(re.getInt("errcode", 0)!=0)
			throw new IllegalArgumentException("reflushAccessToken FAIL : " + str);
        String token = re.getString("access_token");
        int expires = re.getInt("expires_in") - 200;//微信默认超时为7200秒，此处设置稍微短一点
        accessTokenStore.save(token, expires, System.currentTimeMillis());
    }

    @Override
    public NutMap genJsSDKConfig(String url, String... jsApiList) {
        String jt = this.getJsapiTicket();
        long timestamp = System.currentTimeMillis();
        String nonceStr = R.UU64();

        String str = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s", jt, nonceStr, timestamp, url);
        String signature = Lang.sha1(str);

        NutMap map = new NutMap();
        map.put("appId", appid);
        map.put("timestamp", timestamp);
        map.put("nonceStr", nonceStr);
        map.put("signature", signature);
        map.put("jsApiList", jsApiList);
        return map;
    }

    public void configure(Object obj) {
        BeanConfigures.configure(this, obj);
    }

}
