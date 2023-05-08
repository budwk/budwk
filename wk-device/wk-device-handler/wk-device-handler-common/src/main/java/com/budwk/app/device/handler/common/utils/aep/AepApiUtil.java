package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.utils.aep.model.AepResult;
import com.budwk.app.device.handler.common.utils.aep.model.BaseRequest;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zyang
 */
public class AepApiUtil {
    private static final Log log = Logs.get();

    public static AepResult doRequest(BaseRequest request) {
        try {
            Request httpRequest = Request.create(String.format("%s%s", request.getBaseUrl(), request.getPath()), request.getMethod());
            httpRequest.setHeader(Header.create(request.getHeader()));
            httpRequest.setParams(request.getParams());
            httpRequest.setData(request.getBody());
            Sender sender = Sender.create(httpRequest);
            sender.setTimeout(request.getTimeout());
            Response response = sender.send();
            Header header = response.getHeader();

            if (response.isOK()) {
                String jsonStr = response.getContent();
                log.debugf("response content \r\n %s", Json.toJson(jsonStr));
                NutMap jsonObject = Json.fromJson(NutMap.class,jsonStr);
                if (jsonObject == null) {
                    return null;
                }
                String msg = jsonObject.getString("msg");
                if (Strings.isBlank(msg)) {
                    msg = jsonObject.getString("desc");
                }
                return new AepResult(jsonObject.getInt("code"), msg, jsonObject.get("result"));
            } else {
                return new AepResult(response.getStatus(), header.get("x-ag-message", "ERROR"), null);
            }
        } catch (Exception e) {
            log.error(e);
            return new AepResult(-1, e.getMessage(), null);
        }
    }

    public static String sign(String secret, Map header, Map param, byte[] body) throws Exception {
        if (secret == null) {
            throw new Exception("Secret cannot be null");
        } else if (header == null) {
            throw new Exception("Headers cannot be null");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("application").append(":").append(header.get("application")).append("\n");
            sb.append("timestamp").append(":").append(header.get("timestamp")).append("\n");
            if (param != null) {
                Set<String> keys = new TreeSet(param.keySet());
                Iterator var6 = keys.iterator();

                while (var6.hasNext()) {
                    String s = Strings.sBlank(var6.next());
                    Object v = param.get(s);

                    if(v instanceof Iterable){
                        Iterator iterator = ((Iterable) v).iterator();
                        while (iterator.hasNext()) {
                            String val = Strings.sBlank(iterator.next());
                            sb.append(s).append(":").append(val == null ? "" : val).append("\n");
                        }
                    }else{
                        String val = Strings.sBlank(v);
                        sb.append(s).append(":").append(val == null ? "" : val).append("\n");
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            if (body != null && body.length > 0) {
                baos.write(body);
                baos.write("\n".getBytes(StandardCharsets.UTF_8));
            }

            return new String(Base64.getEncoder().encode(encryptHMAC(secret, baos.toByteArray())));
        }
    }

    public static byte[] encryptHMAC(String secret, byte[] data) {
        byte[] bytes = null;

        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSha1");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data);
        } catch (Exception var5) {
            var5.printStackTrace(System.err);
        }

        return bytes;
    }
}
