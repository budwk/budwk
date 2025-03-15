package com.budwk.starter.wechat.spi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.budwk.starter.wechat.bean.WxInMsg;
import com.budwk.starter.wechat.bean.WxOutMsg;

/**
 *  @author wendal(wendal1985@gmail.com)
 *  @author wizzercn(wizzer.cn@gmail.com)
 */
public interface WxBaseApi {

    WxResp send(WxOutMsg out);
    
    WxInMsg parse(HttpServletRequest req);
    
    void handle(HttpServletRequest req, HttpServletResponse resp, WxHandler handler);
    
    List<String> getcallbackip();

    void setPayBase(String url);

    void setWxBase(String url);

    void setMpBase(String url);
}
