package com.budwk.app.uc.services;


import com.budwk.starter.common.exception.CaptchaException;
import com.budwk.starter.common.exception.EmailException;
import com.budwk.starter.common.exception.SmsException;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.sms.enums.SmsType;
import org.nutz.lang.util.NutMap;

/**
 * 验证码服务
 *
 * @author wizzer@qq.com
 */
public interface ValidateService {
    /**
     * 获取验证码
     *
     * @return
     */
    Result<NutMap> getCaptcha();

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号码
     * @param type   验证码类型(register-注册验证码/login-登录验证码/password-找回密码)
     * @throws SmsException
     */
    void getSmsCode(String mobile, SmsType type) throws SmsException;

    /**
     * 发送邮件验证码
     *
     * @param subject   邮件标题
     * @param loginname 用户名
     * @param email     收件人信箱
     * @throws EmailException
     */
    void getEmailCode(String subject, String loginname, String email) throws EmailException;

    /**
     * 核验验证码
     *
     * @param key  验证码的key
     * @param code 验证码的值
     * @throws CaptchaException
     */
    void checkCode(String key, String code) throws CaptchaException;

    /**
     * 核验短信验证码
     *
     * @param mobile 手机号码
     * @param code   验证码值
     * @throws SmsException
     */
    void checkSMSCode(String mobile, String code) throws SmsException;

    /**
     * 核验邮件验证码
     *
     * @param loginname 用户名
     * @param code      验证码值
     * @throws EmailException
     */
    void checkEmailCode(String loginname, String code) throws EmailException;
}
