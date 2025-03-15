package com.budwk.starter.wechat.spi;

import org.nutz.lang.util.NutMap;

public interface WxNewTmplApi {
    /**
     * 选用模板,从公共模板库中选用模板，到私有模板库中
     *
     * @param tid       模板标题 id，可通过getPubTemplateTitleList接口获取，也可登录公众号后台查看获取
     * @param kidList   开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如 [3,5,4] 或 [4,5,3]），最多支持5个，最少2个关键词组合
     * @param sceneDesc 服务场景描述，15个字以内
     */
    WxResp newtmpl_addtemplate(String tid, int[] kidList, String sceneDesc);

    /**
     * 删除模板,删除私有模板库中的模板
     *
     * @param priTmplId 要删除的模板id
     * @return
     */
    WxResp newtmpl_deleteTemplate(String priTmplId);

    /**
     * 获取公众号类目
     *
     * @return
     */
    WxResp newtmpl_getCategory();

    /**
     * 获取公共模板下的关键词列表
     *
     * @param tid 模板标题 id
     * @return
     */
    WxResp newtmpl_getPubTemplateKeyWordsById(String tid);

    /**
     * 获取类目下的公共模板
     *
     * @param ids   类目 id，多个用逗号隔开
     * @param start 用于分页，表示从 start 开始，从 0 开始计数
     * @param limit 用于分页，表示拉取 limit 条记录，最大为 30
     * @return
     */
    WxResp newtmpl_getPubTemplateTitleList(String ids, int start, int limit);

    /**
     * 获取私有模板列表
     *
     * @return
     */
    WxResp newtmpl_getTemplateList();

    /**
     * 发送订阅通知
     *
     * @param touser      接收者（用户）的 openid
     * @param template_id 所需下发的订阅模板id
     * @param page        跳转网页时填写
     * @param miniprogram 跳转小程序时填写，格式如{ "appid": "pagepath": { "value": any } }
     * @param data        模板内容，格式形如 { "key1": { "value": any }, "key2": { "value": any } }
     * @return
     */
    WxResp newtmpl_send(String touser, String template_id, String page, NutMap miniprogram, NutMap data);
}
