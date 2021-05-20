package com.budwk.starter.email;

import com.budwk.starter.common.exception.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Date;

/**
 * @author wizzer@qq.com
 */
@IocBean
public class EmailSendServer {
    private static final Log log = Logs.get();

    protected static final String PRE = "email.";

    @PropDoc(value = "Email 是否启用", defaultValue = "")
    public static final String PROP_EMAIL_ENABLE = PRE + "enable";

    @PropDoc(value = "Email 模版中的联系域名", defaultValue = "")
    public static final String PROP_EMAIL_DOMAIN = PRE + "template.domain";

    @PropDoc(value = "Email 模版中的落款", defaultValue = "")
    public static final String PROP_EMAIL_AUTHOR = PRE + "template.author";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void sendCode(String subject, String email, String text, long time) {
        try {
            if (!conf.getBoolean(PROP_EMAIL_ENABLE, false)) {
                throw new EmailException("邮件发送功能未启用");
            }
            //ImageHtmlEmail 请不要使用注入模式，每次使用都需要去Ioc取一下
            ImageHtmlEmail htmlEmail = ioc.get(ImageHtmlEmail.class);
            htmlEmail.setSubject(subject);
            htmlEmail.addTo(email);
            htmlEmail.setHtmlMsg(EmailTemplate.tplCode.replace("${code}", text).
                    replace("${time}", Times.format("yyyy-MM-dd HH:mm:ss", new Date(time)))
                    .replace("${author}", conf.get(PROP_EMAIL_AUTHOR, ""))
                    .replace("${title}", subject)
                    .replace("${http}", conf.get(PROP_EMAIL_DOMAIN, ""))
                    .replace("${domain}",
                            conf.get(PROP_EMAIL_DOMAIN, "").toLowerCase()
                                    .replace("http://", "").replace("https://", ""))
            );
            log.debug("开始邮件发送..");
            htmlEmail.buildMimeMessage();
            htmlEmail.sendMimeMessage();
            log.debug("邮件发送完毕-请查收！注意有可能进入垃圾收件箱");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                throw new EmailException(e.getMessage());
            } else {
                throw new EmailException("邮件发送失败");
            }
        }
    }

    public void sendMsg(String name, String subject, String email, String text) {
        try {
            if (!conf.getBoolean(PROP_EMAIL_ENABLE, false)) {
                throw new EmailException("邮件发送功能未启用");
            }
            //ImageHtmlEmail 请不要使用注入模式，每次使用都需要去Ioc取一下
            ImageHtmlEmail htmlEmail = ioc.get(ImageHtmlEmail.class);
            htmlEmail.setSubject(subject);
            htmlEmail.addTo(email);
            htmlEmail.setHtmlMsg(EmailTemplate.tplMsg.replace("${text}", text)
                    .replace("${author}", conf.get(PROP_EMAIL_AUTHOR, ""))
                    .replace("${title}", subject)
                    .replace("${name}", name)
                    .replace("${http}", conf.get(PROP_EMAIL_DOMAIN, ""))
                    .replace("${domain}",
                            conf.get(PROP_EMAIL_DOMAIN, "").toLowerCase()
                                    .replace("http://", "").replace("https://", ""))
            );
            log.debug("开始邮件发送..");
            htmlEmail.buildMimeMessage();
            htmlEmail.sendMimeMessage();
            log.debug("邮件发送完毕-请查收！注意有可能进入垃圾收件箱");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                throw new EmailException(e.getMessage());
            } else {
                throw new EmailException("邮件发送失败");
            }
        }
    }
}
