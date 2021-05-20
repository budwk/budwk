package com.budwk.starter.sms;

import com.budwk.starter.common.exception.SmsException;
import com.budwk.starter.sms.enums.SmsType;
import com.budwk.starter.sms.service.ISmsService;
import com.budwk.starter.sms.service.impl.SmsBudiotServiceImpl;
import com.budwk.starter.sms.service.impl.SmsTencentServiceImpl;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class SmsSendServer {
    private static final Log log = Logs.get();

    protected static final String PRE = "sms.";

    @PropDoc(value = "SMS 短信服务是否启用", defaultValue = "")
    public static final String PROP_SMS_ENABLE = PRE + "enable";

    @PropDoc(value = "SMS 短信服务商", defaultValue = "")
    public static final String PROP_SMS_PROVIDER = PRE + "provider";

    @PropDoc(value = "SMS 腾讯云 secret-id", defaultValue = "")
    public static final String PROP_SMS_TENCENT_SECRET_ID = PRE + "tencent.secret-id";

    @PropDoc(value = "SMS 腾讯云 secret-key", defaultValue = "")
    public static final String PROP_SMS_TENCENT_SECRET_KEY = PRE + "tencent.secret-key";

    @PropDoc(value = "SMS 腾讯云 应用appid", defaultValue = "")
    public static final String PROP_SMS_TENCENT_APPID = PRE + "tencent.appid";

    @PropDoc(value = "SMS 腾讯云 短信签名", defaultValue = "")
    public static final String PROP_SMS_TENCENT_SIGN = PRE + "tencent.sign";

    @PropDoc(value = "SMS 注册验证码模版", defaultValue = "")
    public static final String PROP_SMS_TEMPLATE_REGISTER = PRE + "template.register";

    @PropDoc(value = "SMS 登录验证码模版", defaultValue = "")
    public static final String PROP_SMS_TEMPLATE_LOGIN = PRE + "template.login";

    @PropDoc(value = "SMS 找回密码验证码模版", defaultValue = "")
    public static final String PROP_SMS_TEMPLATE_PASSWORD = PRE + "template.password";

    @PropDoc(value = "SMS 短信通知模版", defaultValue = "")
    public static final String PROP_SMS_TEMPLATE_MESSAGE = PRE + "template.message";

    @PropDoc(value = "SMS 萌发云 appId", defaultValue = "")
    public static final String PROP_SMS_BUDIOT_APPID = PRE + "budiot.app-id";

    @PropDoc(value = "SMS 萌发云 appKey", defaultValue = "")
    public static final String PROP_SMS_BUDIOT_APP_KEY = PRE + "budiot.app-key";

    @PropDoc(value = "SMS 萌发云 短信签名", defaultValue = "")
    public static final String PROP_SMS_BUDIOT_SIGN = PRE + "budiot.sign";

    @PropDoc(value = "SMS 萌发云 调用地址", defaultValue = "")
    public static final String PROP_SMS_BUDIOT_SERVER = PRE + "budiot.server";

    @PropDoc(value = "SMS 萌发云 SP-CODE", defaultValue = "")
    public static final String PROP_SMS_BUDIOT_SPCODE = PRE + "budiot.sp-code";

    private ISmsService smsService;

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        if (conf.getBoolean(PROP_SMS_ENABLE, false)) {
            if ("tencent".equalsIgnoreCase(conf.get(PROP_SMS_PROVIDER))) {
                smsService = ioc.get(SmsTencentServiceImpl.class);
            }
        }
    }


    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @param text   验证码
     * @param type   类型
     * @return true发送成功
     * @throws SmsException
     */
    public void sendCode(String mobile, String text, SmsType type) throws SmsException {
        if (!conf.getBoolean(PROP_SMS_ENABLE, false)) {
            throw new SmsException("短信发送功能未启用");
        }
        smsService.sendCode(mobile, text, type);
    }

    /**
     * 发送短信通知
     *
     * @param mobile 手机号码(腾讯云最大200)
     * @param param  模板参数值
     * @return
     * @throws SmsException
     */
    public void sendMsg(String[] mobile, String[] param) throws SmsException {
        if (!conf.getBoolean(PROP_SMS_ENABLE, false)) {
            throw new SmsException("短信发送功能未启用");
        }
        smsService.sendMsg(mobile, param);
    }
}
