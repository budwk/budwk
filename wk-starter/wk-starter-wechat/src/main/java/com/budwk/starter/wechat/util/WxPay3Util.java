package com.budwk.starter.wechat.util;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import com.budwk.starter.wechat.bean.WxPay3Response;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * 微信支付V3工具类
 * 参考项目 https://github.com/Javen205/IJPay
 * 实例详见 https://github.com/budwk/budwk-nutzboot
 *
 * @author wizzer@qq.com
 */
public class WxPay3Util {
    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";
    private static final int TAG_LENGTH_BIT = 128;
    private static final String OS = System.getProperty("os.name") + "/" + System.getProperty("os.version");
    private static final String VERSION = System.getProperty("java.version");

    /**
     * V3签名算法
     *
     * @param privateKey
     * @param data
     * @return
     * @throws Exception
     */
    public static String createSign(String data, PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(data.getBytes(StandardCharsets.UTF_8));
        return org.nutz.repo.Base64.encodeToString(sign.sign(), false);
    }

    /**
     * V3签名算法 通过私钥路径
     *
     * @param keyPath
     * @param data
     * @return
     * @throws Exception
     */
    public static String createSign(String data, String keyPath) throws Exception {
        if (Strings.isBlank(data)) {
            return null;
        } else {
            PrivateKey privateKey = getPrivateKey(keyPath);
            return encryptByPrivateKey(data, privateKey);
        }
    }

    /**
     * 私钥签名
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signed = signature.sign();
        return Strings.sNull(Base64.getEncoder().encodeToString(signed));
    }

    /**
     * 通过文件路径获取私钥
     *
     * @param keyPath
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String keyPath) throws Exception {
        String originalKey = new String(Files.readBytes(keyPath), StandardCharsets.UTF_8.name());
        return loadPrivateKey(originalKey);
    }

    /**
     * 获取证书序列号
     *
     * @param v3CertPath
     * @return
     */
    public static String getCertSerialNo(String v3CertPath) {
        X509Certificate certificate = WxPay3Util.getCertificate(Streams.fileIn(v3CertPath));
        return certificate.getSerialNumber().toString(16).toUpperCase();
    }

    /**
     * 获取证书
     *
     * @param inputStream
     * @return
     */
    public static X509Certificate getCertificate(InputStream inputStream) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);
            cert.checkValidity();
            return cert;
        } catch (CertificateExpiredException var3) {
            throw new RuntimeException("证书已过期", var3);
        } catch (CertificateNotYetValidException var4) {
            throw new RuntimeException("证书尚未生效", var4);
        } catch (CertificateException var5) {
            throw new RuntimeException("无效的证书", var5);
        }
    }

    /**
     * 生成 V3 Authorization
     *
     * @param method
     * @param urlSuffix
     * @param mchId
     * @param serialNo
     * @param privateKey
     * @param body
     * @param nonceStr
     * @param timestamp
     * @param authType
     * @return
     * @throws Exception
     */
    public static String buildAuthorization(String method, String urlSuffix, String mchId,
                                            String serialNo, PrivateKey privateKey, String body, String nonceStr,
                                            long timestamp, String authType) throws Exception {
        // 构建签名参数
        String buildSignMessage = buildSignMessage(method, urlSuffix, timestamp, nonceStr, body);
        String signature = createSign(buildSignMessage, privateKey);
        // 根据平台规则生成请求头 authorization
        return getAuthorization(mchId, serialNo, nonceStr, String.valueOf(timestamp), signature, authType);
    }

    /**
     * 生成 V3 Authorization
     *
     * @param method
     * @param url
     * @param mchId
     * @param serialNo
     * @param keyPath
     * @param body
     * @param nonceStr
     * @param timestamp
     * @param authType
     * @return
     * @throws Exception
     */
    public static String buildAuthorization(String method, String url, String mchId, String serialNo, String keyPath, String body, String nonceStr, long timestamp, String authType) throws Exception {
        String buildSignMessage = buildSignMessage(method, url, timestamp, nonceStr, body);
        String signature = createSign(buildSignMessage, keyPath);
        return getAuthorization(mchId, serialNo, nonceStr, String.valueOf(timestamp), signature, authType);
    }

    /**
     * 生成 V3 Authorization
     *
     * @param mchId
     * @param serialNo
     * @param nonceStr
     * @param timestamp
     * @param signature
     * @param authType
     * @return
     */
    public static String getAuthorization(String mchId, String serialNo, String nonceStr, String timestamp, String signature, String authType) {
        Map<String, String> params = new HashMap<>(5);
        params.put("mchid", mchId);
        params.put("serial_no", serialNo);
        params.put("nonce_str", nonceStr);
        params.put("timestamp", timestamp);
        params.put("signature", signature);
        return authType.concat(" ").concat(createLinkString(params, ",", false, true));
    }

    /**
     * 拼接URL请求字符串
     *
     * @param params  参数
     * @param connStr 连接字符串
     * @param encode  是否URL编码
     * @param quotes  是否加引号
     * @return
     */
    public static String createLinkString(Map<String, String> params, String connStr, boolean encode, boolean quotes) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            // 不包括最后一个&字符
            if (i == keys.size() - 1) {
                if (quotes) {
                    content.append(key).append("=").append('"').append(encode ? getUrlEncode(value) : value).append('"');
                } else {
                    content.append(key).append("=").append(encode ? getUrlEncode(value) : value);
                }
            } else {
                if (quotes) {
                    content.append(key).append("=").append('"').append(encode ? getUrlEncode(value) : value).append('"').append(connStr);
                } else {
                    content.append(key).append("=").append(encode ? getUrlEncode(value) : value).append(connStr);
                }
            }
        }
        return content.toString();
    }

    /**
     * 获取URLEncode
     *
     * @param src 原始字符串
     * @return
     */
    public static String getUrlEncode(String src) {
        try {
            return URLEncoder.encode(src, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构造签名字符串
     *
     * @param method    GET/POST等请求方法
     * @param url       URL
     * @param timestamp 时间戳
     * @param nonceStr  随机字符串
     * @param body      body
     * @return
     */
    public static String buildSignMessage(String method, String url, long timestamp, String nonceStr, String body) {
        return method + "\n"
                + url + "\n"
                + String.valueOf(timestamp) + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * 构造签名字符串
     *
     * @param timestamp 时间戳
     * @param nonceStr  随机字符串
     * @param body      body
     * @return
     */
    public static String buildSignMessage(String timestamp, String nonceStr, String body) {
        return timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * 获取jsapi签名内容
     *
     * @param appId
     * @param prepay_id
     * @param keyPath
     * @return
     * @throws Exception
     */
    public static NutMap getJsapiSignMessage(String appId, String prepay_id, String keyPath) throws Exception {
        String packageStr = "prepay_id=" + prepay_id;
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = R.UU32().toUpperCase();
        String signMessage = appId + "\n"
                + timeStamp + "\n"
                + nonceStr + "\n"
                + packageStr + "\n";
        String sign = WxPay3Util.createSign(signMessage, keyPath);
        NutMap params = NutMap.NEW();
        params.put("appId", appId);
        params.put("timeStamp", timeStamp);
        params.put("nonceStr", nonceStr);
        params.put("package", "prepay_id=" + prepay_id);
        params.put("signType", "RSA");
        params.put("paySign", sign);
        return params;
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr 私钥字符串
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            String privateKey = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] buffer = org.nutz.repo.Base64.decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 组装请求头
     *
     * @param authorization authorization
     * @param serialNumber  证书序列号
     * @return
     */
    public static Map<String, String> getHeaders(String authorization, String serialNumber) {
        Map<String, String> headers = getBaseHeaders(authorization);
        headers.put("Content-Type", "application/json");
        if (Strings.isNotBlank(serialNumber)) {
            headers.put("Wechatpay-Serial", serialNumber);
        }
        return headers;
    }


    /**
     * 组装请求头
     *
     * @param authorization authorization
     * @return
     */
    public static Map<String, String> getBaseHeaders(String authorization) {
        String userAgent = String.format(
                "WeChatPay-IJPay-HttpClient/%s (%s) Java/%s",
                WxPay3Util.class.getPackage().getImplementationVersion(),
                OS,
                VERSION == null ? "Unknown" : VERSION);

        Map<String, String> headers = new HashMap<>(5);
        headers.put("Accept", "application/json");
        headers.put("Authorization", authorization);
        headers.put("User-Agent", userAgent);
        return headers;
    }

    /**
     * 验证微信服务返回的数据签名
     *
     * @param wxPay3Response 微信服务器返回的对象
     * @param platfromCert   平台证书内容
     * @return
     * @throws Exception
     */
    public static boolean verifySignature(WxPay3Response wxPay3Response, String platfromCert) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(platfromCert.getBytes());
        // 获取平台证书序列号
        X509Certificate certificate = getCertificate(inputStream);
        String body = wxPay3Response.getBody();
        String signature = wxPay3Response.getHeader().get("Wechatpay-Signature");
        String nonce = wxPay3Response.getHeader().get("Wechatpay-Nonce");
        String timestamp = wxPay3Response.getHeader().get("Wechatpay-Timestamp");
        return verifySignature(signature, body, nonce, timestamp, certificate.getPublicKey());
    }

    /**
     * v3 支付异步通知验证签名
     *
     * @param serialNo     证书序列号
     * @param body         异步通知密文
     * @param signature    签名
     * @param nonce        随机字符串
     * @param timestamp    时间戳
     * @param key          V3API密钥
     * @param platfromCert 平台证书
     * @return 异步通知明文
     * @throws Exception 异常信息
     */
    public static String verifyNotify(String serialNo, String body, String signature, String nonce,
                                      String timestamp, String key, String platfromCert) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(platfromCert.getBytes());
        // 获取平台证书序列号
        X509Certificate certificate = getCertificate(inputStream);
        String serialNumber = certificate.getSerialNumber().toString(16).toUpperCase();
        // 验证证书序列号
        if (serialNumber.equals(serialNo)) {
            boolean verifySignature = verifySignature(signature, body, nonce, timestamp, certificate.getPublicKey());
            if (verifySignature) {
                NutMap resMap = Json.fromJson(NutMap.class, body);
                NutMap resource = resMap.getAs("resource", NutMap.class);
                String cipherText = resource.getString("ciphertext");
                String nonceStr = resource.getString("nonce");
                String associatedData = resource.getString("associated_data");
                // 密文解密
                return decryptToString(
                        key.getBytes(StandardCharsets.UTF_8),
                        associatedData.getBytes(StandardCharsets.UTF_8),
                        nonceStr.getBytes(StandardCharsets.UTF_8),
                        cipherText
                );
            }
        }
        return null;
    }

    /**
     * 证书和回调报文解密
     *
     * @param associatedData associated_data
     * @param nonce          nonce
     * @param cipherText     ciphertext
     * @return
     * @throws GeneralSecurityException 异常
     */
    public static String decryptToString(byte[] aesKey, byte[] associatedData, byte[] nonce, String cipherText) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(org.nutz.repo.Base64.decode(cipherText)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 验证签名
     *
     * @param signature 待验证的签名
     * @param body      应答主体
     * @param nonce     随机串
     * @param timestamp 时间戳
     * @param publicKey 微信支付平台公钥
     * @return 签名结果
     * @throws Exception 异常信息
     */
    public static boolean verifySignature(String signature, String body, String nonce, String timestamp, PublicKey publicKey) throws Exception {
        String buildSignMessage = buildSignMessage(timestamp, nonce, body);
        return checkByPublicKey(buildSignMessage, signature, publicKey);
    }

    /**
     * 公钥验证签名
     *
     * @param data      需要加密的数据
     * @param sign      签名
     * @param publicKey 公钥
     * @return 验证结果
     * @throws Exception 异常信息
     */
    public static boolean checkByPublicKey(String data, String sign, PublicKey publicKey) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256WithRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(org.nutz.repo.Base64.decode(sign.getBytes(StandardCharsets.UTF_8)));
    }
}
