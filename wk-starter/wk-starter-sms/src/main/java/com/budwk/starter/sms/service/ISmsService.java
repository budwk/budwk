package com.budwk.starter.sms.service;

import com.budwk.starter.common.exception.SmsException;
import com.budwk.starter.sms.enums.SmsType;

/**
 * @author wizzer@qq.com
 */
public interface ISmsService {

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @param text   验证码
     * @param type   类型
     * @return true发送成功
     * @throws SmsException
     */
    void sendCode(String mobile, String text, SmsType type) throws SmsException;

    /**
     * 发送短信通知
     *
     * @param mobile 手机号码(腾讯云最大200)
     * @param param  模板参数值
     * @return
     * @throws SmsException
     */
    void sendMsg(String[] mobile, String[] param) throws SmsException;
}
