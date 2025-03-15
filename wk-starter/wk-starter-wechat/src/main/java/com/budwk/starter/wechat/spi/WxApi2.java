package com.budwk.starter.wechat.spi;

/**
 * v2版的API, 基本上不抛异常
 *
 * @author wendal(wendal1985@gmail.com)
 */
public interface WxApi2 extends WxBaseApi, WxMassApi, WxUserApi, WxQrApi, WxTemplateMsgApi,
        WxMediaApi, WxMenuApi, WxAccessTokenApi, WxJsapiTicketApi, WxShakeApi, WXAccountApi, WxMaterialApi,
        WxCustomServiceApi, WxPayApi, WxCardApi, WxCardTicketApi, WxNewTmplApi {

}
