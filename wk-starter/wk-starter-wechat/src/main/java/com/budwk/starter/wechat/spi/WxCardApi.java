package com.budwk.starter.wechat.spi;

import org.nutz.lang.util.NutMap;

/**
 * 
 * @author JinYi(wdhlzd@163.com)
 * 
 */
public interface WxCardApi {

    /**
     * 微信卡券：创建卡券
     *
     * @author JinYi
     * @param card
     * @return
     */
    WxResp card_create(NutMap card);

    /**
     * 微信卡券：投放卡券，创建二维码
     *
     * @author JinYi
     * @param body
     * @return
     */
    WxResp card_qrcode_create(NutMap body);

    /**
     * 微信卡券：查询Code
     *
     * @author JinYi
     * @param code 卡券Code码，一张卡券的唯一标识，必填
     * @param cardId 卡券ID代表一类卡券，null表示不填此参数。自定义code卡券必填
     * @param checkConsume 是否校验code核销状态，填入true和false时的code异常状态返回数据不同，null表示不填此参数
     * @return
     */
    WxResp card_code_get(String code, String cardId, Boolean checkConsume);

    /**
     * 微信卡券：查询Code
     *
     * @author JinYi
     * @param code 卡券Code码，一张卡券的唯一标识，必填
     * @param cardId 卡券ID代表一类卡券，null表示不填此参数。自定义code卡券必填
     * @return
     */
    WxResp card_code_get(String code, String cardId);

    /**
     * 微信卡券：查询Code
     *
     * @author JinYi
     * @param code 卡券Code码，一张卡券的唯一标识，必填
     * @return
     */
    WxResp card_code_get(String code);

    /**
     * 微信卡券：核销卡券<br/>
     * 建议在调用核销卡券接口之前调用查询Code接口<br/>
     * 以便在核销之前对非法状态的Code（如转赠中、已删除、已核销等）做出处理
     *
     * @author JinYi
     * @param code 需核销的Code码，必填
     * @param cardId 卡券ID代表一类卡券，null表示不填此参数。创建卡券时use_custom_code填写true时必填。非自定义Code不必填写
     * @return
     */
    WxResp card_code_consume(String code, String cardId);

    /**
     * 微信卡券：核销卡券<br/>
     * 建议在调用核销卡券接口之前调用查询Code接口<br/>
     * 以便在核销之前对非法状态的Code（如转赠中、已删除、已核销等）做出处理
     *
     * @author JinYi
     * @param code 需核销的Code码，必填
     * @return
     */
    WxResp card_code_consume(String code);
}
