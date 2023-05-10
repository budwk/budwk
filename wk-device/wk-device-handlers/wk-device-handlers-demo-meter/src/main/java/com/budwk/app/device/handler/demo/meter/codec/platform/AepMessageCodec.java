package com.budwk.app.device.handler.demo.meter.codec.platform;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.MessageCodec;
import com.budwk.app.device.handler.common.codec.context.DecodeContext;
import com.budwk.app.device.handler.common.codec.context.EncodeContext;
import com.budwk.app.device.handler.common.codec.exception.MessageCodecException;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.codec.result.EncodeResult;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.handler.common.message.codec.HttpMessage;
import com.budwk.app.device.handler.common.message.codec.TcpMessage;
import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import com.budwk.app.device.handler.common.utils.aep.AepPlatformHelper;
import com.budwk.app.device.handler.demo.meter.codec.DecodeProcessor;
import com.budwk.app.device.handler.demo.meter.utils.ByteParseUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

/**
 * @author wizzer.cn
 */
public class AepMessageCodec implements MessageCodec {

    @Override
    public DecodeResult decode(DecodeContext context) {
        HttpMessage message = (HttpMessage) context.getMessage();
        NutMap data = NutMap.WRAP(new String(message.getPayload()));
        if (Lang.isEmpty(data)) {
            throw new MessageCodecException("数据为空");
        }
        String notifyType = data.getString("messageType", "");
        String key = "commandResponse".equalsIgnoreCase(notifyType) ? "result" : "data";
        if ("commandResponse".equalsIgnoreCase(notifyType)) {
            // 指令回复
            return null;
        }
        NutMap payload = data.getAs("payload", NutMap.class);
        byte[] bytes = null;
        if (Lang.isNotEmpty(payload)) {
            if (payload.containsKey("APPdata")) {
                // 说明是透传的
                bytes = Base64.getDecoder().decode(payload.getString("APPdata"));
            } else {
                String serviceId = payload.getString("serviceId");
                if ("Gas".equalsIgnoreCase(serviceId)) {
                    NutMap serviceData = payload.getAs("serviceData", NutMap.class);
                    if (Lang.isNotEmpty(serviceData)) {
                        bytes = ByteConvertUtil.hexToBytes(serviceData.getString(key));
                    }
                }
            }
            if (null == bytes) {
                return null;
            }
            message.setPayload(bytes);
            return getDecodeProcessor(context).process();
        }

        return null;
    }

    private DecodeProcessor getDecodeProcessor(DecodeContext context) {
        return new DecodeProcessor(context) {
            @Override
            public void sendToDevice(byte[] replyBytes) {
                DeviceOperator deviceOperator = context.getDevice();
                ProductInfo product = deviceOperator.getProduct();
                NutMap authConfig = product.getAuthConfig();
                NutMap fileConfig = product.getFileConfig();
                AepPlatformHelper aepPlatformHelper = new AepPlatformHelper(fileConfig);
                if (Strings.sBlank(authConfig.get("hasProfile")).equals("true")) {
                    aepPlatformHelper.createCommandProfile(authConfig.getString("masterKey"), authConfig.getString("productId"), (String) deviceOperator.getProperty("platformDeviceId"), ByteConvertUtil.bytesToHex(replyBytes));
                } else {
                    aepPlatformHelper.createCommand(authConfig.getString("masterKey"), authConfig.getString("productId"), (String) deviceOperator.getProperty("platformDeviceId"), ByteConvertUtil.bytesToHex(replyBytes));
                }
                // 标记已经发送过了，调用这个用来保存发送日志
                context.send(new HttpMessage() {
                    @Override
                    public String payloadAsString() {
                        return ByteConvertUtil.bytesToHex(replyBytes);
                    }
                });
            }
        };
    }

    @Override
    public EncodeResult encode(EncodeContext context) {
        CommandInfo commandInfo = context.getCommandInfo();
        byte[] bytes = ByteParseUtil.buildCommand(commandInfo, context.getDeviceOperator());
        DeviceOperator deviceOperator = context.getDeviceOperator();
        ProductInfo product = deviceOperator.getProduct();
        NutMap authConfig = product.getAuthConfig();
        NutMap fileConfig = product.getFileConfig();
        AepPlatformHelper aepPlatformHelper = new AepPlatformHelper(fileConfig);
        String result;
        if (Strings.sBlank(authConfig.get("hasProfile")).equals("true")) {
            result = aepPlatformHelper.createCommandProfile(authConfig.getString("masterKey"), authConfig.getString("productId"), (String) deviceOperator.getProperty("iotDevId"), ByteConvertUtil.bytesToHex(bytes));
        } else {
            result = aepPlatformHelper.createCommand(authConfig.getString("masterKey"), authConfig.getString("productId"), (String) deviceOperator.getProperty("iotDevId"), ByteConvertUtil.bytesToHex(bytes));
        }
        TcpMessage tcpMessage = new TcpMessage(bytes);
        return EncodeResult.createDefault(true, Arrays.asList(tcpMessage));
    }
}
