package com.budwk.app.uc.services.impl;

import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.app.uc.services.ValidateService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.CaptchaException;
import com.budwk.starter.common.exception.EmailException;
import com.budwk.starter.common.exception.SmsException;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.email.EmailSendServer;
import com.budwk.starter.sms.SmsSendServer;
import com.budwk.starter.sms.enums.SmsType;
import com.wf.captcha.*;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;

/**
 * @author wizzer@qq.com
 */
@IocBean
@Slf4j
public class ValidateServiceImpl implements ValidateService {

    @Inject
    private RedisService redisService;
    @Inject
    private SmsSendServer smsSendServer;
    @Inject
    private EmailSendServer emailSendServer;
    @Inject
    private SysUserService sysUserProvider;

    /**
     * 获取验证码
     *
     * @return
     */
    public Result<NutMap> getCaptcha() {
        String uuid = R.UU32();
        Sys_user_security security = sysUserProvider.getUserSecurity();
        if (security != null && security.getCaptchaHasEnabled() != null && security.getCaptchaHasEnabled()) {
            String text = "";
            String base64 = null;
            int w = 120;
            int h = 40;
            switch (security.getCaptchaType()) {
                case 1:
                    SpecCaptcha captcha1 = new SpecCaptcha(120, 40, 4);
                    captcha1.setCharType(2);// 纯数字
                    text = captcha1.text();
                    base64 = captcha1.toBase64();
                    break;
                case 2:
                    SpecCaptcha captcha2 = new SpecCaptcha(120, 40, 4);
                    captcha2.setCharType(3);// 纯字母
                    text = captcha2.text();
                    base64 = captcha2.toBase64();
                    break;
                case 3:
                    SpecCaptcha captcha3 = new SpecCaptcha(120, 40, 4);
                    captcha3.setCharType(1);// 字母数字混合
                    text = captcha3.text();
                    base64 = captcha3.toBase64();
                    break;
                case 4:
                    ChineseCaptcha captcha4 = new ChineseCaptcha(120, 40, 4);
                    text = captcha4.text();
                    base64 = captcha4.toBase64();
                    break;
                case 5:
                    ChineseCaptcha captcha5 = new ChineseCaptcha(120, 40, 2);
                    text = captcha5.text();
                    base64 = captcha5.toBase64();
                    break;
                case 11:
                    GifCaptcha captcha11 = new GifCaptcha(120, 40, 4);
                    captcha11.setCharType(2);// 纯数字
                    text = captcha11.text();
                    base64 = captcha11.toBase64();
                    break;
                case 22:
                    GifCaptcha captcha22 = new GifCaptcha(120, 40, 4);
                    captcha22.setCharType(3);// 纯字母
                    text = captcha22.text();
                    base64 = captcha22.toBase64();
                    break;
                case 33:
                    GifCaptcha captcha33 = new GifCaptcha(120, 40, 4);
                    captcha33.setCharType(1);// 字母数字混合
                    text = captcha33.text();
                    base64 = captcha33.toBase64();
                    break;
                case 44:
                    ChineseGifCaptcha captcha44 = new ChineseGifCaptcha(120, 40, 4);
                    text = captcha44.text();
                    base64 = captcha44.toBase64();
                    break;
                case 55:
                    ChineseGifCaptcha captcha55 = new ChineseGifCaptcha(120, 40, 2);
                    text = captcha55.text();
                    base64 = captcha55.toBase64();
                    break;
                case 0:
                default:
                    ArithmeticCaptcha captcha = new ArithmeticCaptcha(w, h);
                    captcha.getArithmeticString();  // 获取运算的公式：3+2=?
                    text = captcha.text();
                    base64 = captcha.toBase64();
                    break;
            }
            redisService.setex(RedisConstant.UCENTER_CAPTCHA + uuid, 180, text);
            return Result.data(NutMap.NEW().addv("key", uuid).addv("code", base64).addv("captchaHasEnabled", true));
        }
        return Result.data(NutMap.NEW().addv("key", "").addv("code", "").addv("captchaHasEnabled", false));
    }

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号码
     * @param type   验证码类型(register-注册验证码/login-登录验证码/password-找回密码)
     * @throws SmsException
     */
    public void getSmsCode(String mobile, SmsType type) throws SmsException {
        String text = R.captchaNumber(4);
        String codeFromRedis = redisService.get(RedisConstant.UCENTER_SMSCODE + mobile + ":LOCK");
        if (Strings.isNotBlank(codeFromRedis)) {
            throw new SmsException("请1分钟之后再试");
        }
        smsSendServer.sendCode(mobile, text, type);
        log.debug("sms code:::" + text);
        redisService.setex(RedisConstant.UCENTER_SMSCODE + mobile, 300, text);
        redisService.setex(RedisConstant.UCENTER_SMSCODE + mobile + ":LOCK", 60, text);
    }

    /**
     * 发送邮件验证码
     *
     * @param subject   邮件标题
     * @param loginname 用户名
     * @param email     收件人信箱
     * @throws EmailException
     */
    public void getEmailCode(String subject, String loginname, String email) throws EmailException {
        String text = R.captchaNumber(6);

        String codeFromRedis = redisService.get(RedisConstant.UCENTER_EMAILCODE + loginname + ":LOCK");
        if (Strings.isNotBlank(codeFromRedis)) {
            throw new EmailException("请1分钟之后再试");
        }
        emailSendServer.sendCode(subject, email, text, System.currentTimeMillis() + 15 * 60 * 1000);
        log.debug("email code:::" + text);
        redisService.setex(RedisConstant.UCENTER_EMAILCODE + loginname, 900, text);
        redisService.setex(RedisConstant.UCENTER_EMAILCODE + loginname + ":LOCK", 60, text);
    }

    /**
     * 核验验证码
     *
     * @param key  验证码的key
     * @param code 验证码的值
     * @throws CaptchaException
     */
    public void checkCode(String key, String code) throws CaptchaException {
        String codeFromRedis = redisService.get(RedisConstant.UCENTER_CAPTCHA + key);

        if (Strings.isBlank(code)) {
            throw new CaptchaException("请输入验证码");
        }
        if (Strings.isEmpty(codeFromRedis)) {
            throw new CaptchaException("验证码已过期");
        }
        if (!Strings.equalsIgnoreCase(code, codeFromRedis)) {
            throw new CaptchaException("验证码不正确");
        }
        redisService.del(RedisConstant.UCENTER_CAPTCHA + key);
    }

    /**
     * 核验短信验证码
     *
     * @param mobile 手机号码
     * @param code   验证码值
     * @throws SmsException
     */
    public void checkSMSCode(String mobile, String code) throws SmsException {
        String codeFromRedis = redisService.get(RedisConstant.UCENTER_SMSCODE + mobile);

        if (Strings.isBlank(code)) {
            throw new SmsException("请输入短信验证码");
        }
        if (Strings.isEmpty(codeFromRedis)) {
            throw new SmsException("短信验证码已过期");
        }
        if (!Strings.equalsIgnoreCase(code, codeFromRedis)) {
            throw new SmsException("短信验证码不正确");
        }
        redisService.del(RedisConstant.UCENTER_SMSCODE + mobile);
    }

    /**
     * 核验邮件验证码
     *
     * @param loginname 用户名
     * @param code      验证码值
     * @throws EmailException
     */
    public void checkEmailCode(String loginname, String code) throws EmailException {
        String codeFromRedis = redisService.get(RedisConstant.UCENTER_EMAILCODE + loginname);
        if (Strings.isBlank(code)) {
            throw new EmailException("请输入验证码");
        }
        if (Strings.isEmpty(codeFromRedis)) {
            throw new EmailException("验证码已过期");
        }
        if (!Strings.equalsIgnoreCase(code, codeFromRedis)) {
            throw new EmailException("验证码不正确");
        }
        redisService.del(RedisConstant.UCENTER_EMAILCODE + loginname);
    }
}
