package com.budwk.starter.sms.service.impl;

import com.budwk.starter.common.exception.SmsException;
import com.budwk.starter.sms.enums.SmsType;
import com.budwk.starter.sms.service.ISmsService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wizzer@qq.com
 */
@IocBean
public class SmsTencentServiceImpl implements ISmsService {
    private static final Log log = Logs.get();
    @Inject
    protected PropertiesProxy conf;

    public void sendCode(String mobile, String text, SmsType type) throws SmsException {
        try {
            Credential cred = new Credential(conf.get("sms.tencent.secret-id"), conf.get("sms.tencent.secret-key"));

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"+86" + mobile};
            req.setPhoneNumberSet(phoneNumberSet1);
            if (SmsType.REGISTER == type) {
                String[] templateParamSet1 = {text, "5"};
                req.setTemplateParamSet(templateParamSet1);
                req.setTemplateID(conf.get("sms.template.register"));
            }
            if (SmsType.LOGIN == type) {
                String[] templateParamSet1 = {text};
                req.setTemplateParamSet(templateParamSet1);
                req.setTemplateID(conf.get("sms.template.login"));
            }
            if (SmsType.PASSWORD == type) {
                String[] templateParamSet1 = {text};
                req.setTemplateParamSet(templateParamSet1);
                req.setTemplateID(conf.get("sms.template.password"));
            }
            req.setSmsSdkAppid(conf.get("sms.tencent.appid"));
            req.setSign(conf.get("sms.tencent.sign"));
            SendSmsResponse resp = client.SendSms(req);
            log.debug(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                throw new SmsException(e.getMessage());
            } else {
                throw new SmsException("短信发送失败");
            }
        }
    }

    /**
     * 发送短信通知
     *
     * @param mobile 手机号码(最多200)
     * @param param  模板参数值
     * @return
     * @throws SmsException
     */
    public void sendMsg(String[] mobile, String[] param) throws SmsException {
        try {
            Credential cred = new Credential(conf.get("sms.tencent.secret-id"), conf.get("sms.tencent.secret-key"));

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            req.setPhoneNumberSet(mobile);
            req.setTemplateParamSet(param);
            req.setTemplateID(conf.get("sms.template.message"));
            req.setSmsSdkAppid(conf.get("sms.tencent.appid"));
            req.setSign(conf.get("sms.tencent.sign"));
            SendSmsResponse resp = client.SendSms(req);
            log.debug(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                throw new SmsException(e.getMessage());
            } else {
                throw new SmsException("短信发送失败");
            }
        }
    }

}
