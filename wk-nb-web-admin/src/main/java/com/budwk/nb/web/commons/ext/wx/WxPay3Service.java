package com.budwk.nb.web.commons.ext.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.wx.models.Wx_pay;
import com.budwk.nb.wx.models.Wx_pay_cert;
import com.budwk.nb.wx.services.WxPayCertService;
import com.budwk.nb.wx.services.WxPayService;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.bean.WxPay3Response;
import org.nutz.weixin.util.WxPay3Api;
import org.nutz.weixin.util.WxPay3Util;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
public class WxPay3Service {
    private static final Log log = Logs.get();
    private static final SimpleDateFormat DATE_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    @Inject
    @Reference(check = false)
    private WxPayCertService wxPayCertService;
    @Inject
    @Reference(check = false)
    private WxPayService wxPayService;


    // 通过商户号获取 wx_pay 对象
    public synchronized Wx_pay getWxPay(String mchid) {
        Wx_pay wxPay = Globals.WxPay3Map.getAs(mchid, Wx_pay.class);
        if (wxPay == null) {
            wxPay = wxPayService.fetch(Cnd.where("mchid", "=", mchid));
            Globals.WxPay3Map.put(wxPay.getMchid(), wxPay);
        }
        checkPlatfromCerts(wxPay);
        return wxPay;
    }

    // 检查及更新平台证书机制
    public void checkPlatfromCerts(Wx_pay wxPay) {
        if (wxPay == null)
            throw new IllegalStateException("Wx_pay is null");
        if (wxPay.getExpire_at() == null || wxPay.getExpire_at() == 0 || wxPay.getExpire_at() < 8 * 3600 * 1000 + System.currentTimeMillis()) {
            getPlatfromCerts(wxPay.getMchid(), wxPay.getV3key(), wxPay.getV3keyPath(), wxPay.getV3certPath());
            wxPay = wxPayService.fetch(Cnd.where("mchid", "=", wxPay.getMchid()));
            Globals.WxPay3Map.put(wxPay.getMchid(), wxPay);
        }
    }

    // jsapi 订单下单
    public WxPay3Response v3_order_jsapi(String mchid, String body) throws Exception {
        log.debug("v3_order_jsapi body::" + body);
        String serialNo = WxPay3Util.getCertSerialNo(getWxPay(mchid).getV3certPath());
        return WxPay3Api.v3_order_jsapi(mchid, serialNo, getWxPay(mchid).getV3keyPath(), body);
    }

    // 通过jsapi 订单号生成js参数
    public NutMap v3_call_jsapi(String mchid, String appid, String prepay_id) throws Exception {
        return WxPay3Util.getJsapiSignMessage(appid, prepay_id, getWxPay(mchid).getV3keyPath());
    }

    // 验证http响应签名结果
    public boolean verifySignature(WxPay3Response wxPay3Response, String mchid) throws Exception {
        Wx_pay_cert wxPayCert = wxPayCertService.fetch(Cnd.where("mchid", "=", mchid).and("serial_no", "=", wxPay3Response.getHeader().get("Wechatpay-Serial")));
        return WxPay3Util.verifySignature(wxPay3Response, wxPayCert.getCertificate());
    }

    // 验证回调通知签名及内容
    public String verifyNotify(String mchid, String serialNo, String body, String signature, String nonce, String timestamp) throws Exception {
        Wx_pay_cert wxPayCert = wxPayCertService.fetch(Cnd.where("mchid", "=", mchid).and("serial_no", "=", serialNo));
        return WxPay3Util.verifyNotify(serialNo, body, signature, nonce, timestamp,
                getWxPay(mchid).getV3key(), wxPayCert.getCertificate());
    }

    /**
     * 请求并保存新证书
     *
     * @param mchid
     * @return
     */
    public void getPlatfromCerts(String mchid, String v3Key, String v3KeyPatch, String v3CertPath) {
        try {
            wxPayCertService.clear(Cnd.where("mchid", "=", mchid).and("expire_at", "<", System.currentTimeMillis()));
            String serialNo = WxPay3Util.getCertSerialNo(v3CertPath);
            WxPay3Response wxPay3Response = WxPay3Api.v3_certificates(mchid, serialNo, v3KeyPatch);
            if (wxPay3Response.getStatus() == 200) {
                NutMap nutMap = Json.fromJson(NutMap.class, wxPay3Response.getBody());
                List<NutMap> list = nutMap.getList("data", NutMap.class);
                for (NutMap cert : list) {
                    Wx_pay_cert wxPayCert = new Wx_pay_cert();
                    wxPayCert.setMchid(mchid);
                    wxPayCert.setEffective_time(cert.getString("effective_time"));
                    wxPayCert.setExpire_time(cert.getString("expire_time"));
                    long expire_at = 0;
                    try {
                        expire_at = Times.parse(DATE_TIME_ZONE, cert.getString("expire_time")).getTime();
                        wxPayCert.setEffective_at(Times.parse(DATE_TIME_ZONE, cert.getString("effective_time")).getTime());
                        wxPayCert.setExpire_at(expire_at);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    wxPayCert.setSerial_no(cert.getString("serial_no"));
                    NutMap encrypt_certificate = cert.getAs("encrypt_certificate", NutMap.class);
                    wxPayCert.setAlgorithm(encrypt_certificate.getString("algorithm"));
                    wxPayCert.setAssociated_data(encrypt_certificate.getString("associated_data"));
                    wxPayCert.setCiphertext(encrypt_certificate.getString("ciphertext"));
                    wxPayCert.setNonce(encrypt_certificate.getString("nonce"));
                    String platformCertificate = WxPay3Util.decryptToString(v3Key.getBytes(StandardCharsets.UTF_8),
                            encrypt_certificate.getString("associated_data").getBytes(StandardCharsets.UTF_8),
                            encrypt_certificate.getString("nonce").getBytes(StandardCharsets.UTF_8),
                            encrypt_certificate.getString("ciphertext")
                    );
                    wxPayCert.setCertificate(platformCertificate);
                    try {
                        wxPayCertService.insert(wxPayCert);
                    } catch (Exception e) {
                        //重复的插入会报错,不管它
                    }
                }
                Wx_pay_cert wxPayCert = wxPayCertService.fetch(Cnd.where("mchid", "=", mchid).orderBy("effective_at", "desc"));
                if (wxPayCert != null) {
                    wxPayService.update(Chain.make("expire_at", wxPayCert.getExpire_at()), Cnd.where("mchid", "=", mchid));
                }
            }
        } catch (Exception e) {
            log.errorf("获取平台证书失败,mchid=%s", mchid, e);
        }
    }
}
