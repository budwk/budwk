package com.budwk.starter.wechat.spi;

import java.io.File;

import org.nutz.resource.NutResource;

/**
 *
 *  @author wendal(wendal1985@gmail.com)
 *  @author wizzer(wizzer.cn@gmail.com)
 *
 */
public interface WxMediaApi {

    WxResp media_upload(String type, File f);
    
    NutResource media_get(String mediaId);

    NutResource media_get_jssdk(String mediaId);
}
