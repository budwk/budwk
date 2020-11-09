package com.budwk.nb.app.commons.ext.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.app.commons.base.Globals;
import com.budwk.nb.wx.models.Wx_mina;
import com.budwk.nb.wx.services.WxMinaService;
import org.nutz.dao.Cnd;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/23
 */
@IocBean(create = "initDefaultMina")
public class MinaService {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private WxMinaService wxMinaService;
    private Wx_mina wxMina;

    public void initDefaultMina() {
        this.wxMina = wxMinaService.fetch(Cnd.where("wxid", "=", Globals.AppDefultWxID));
    }

    public String code2Session(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
        if (wxMina != null) {
            url = String.format(url, wxMina.getAppid(), wxMina.getAppsecret(), code);
            log.debug("url:::" + url);
            Response response = Http.get(url, 20000);
            if (response.isOK()) {
                return response.getContent();
            }
        }
        return null;
    }
}
