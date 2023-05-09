package com.budwk.app.device.handler.demo.meter.codec;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.context.DecodeContext;
import com.budwk.app.device.handler.common.codec.exception.MessageCodecException;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.codec.result.DefaultDecodeResult;
import com.budwk.app.device.handler.common.codec.result.DefaultResponseResult;
import com.budwk.app.device.handler.common.message.DeviceMeterMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;
import com.budwk.app.device.handler.common.message.codec.TcpMessage;
import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import com.budwk.app.device.handler.demo.meter.utils.ByteParseUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wizzer.cn
 */
public class DecodeProcessor {
    private static final Log log = Logs.get();

    private final DecodeContext context;
    private final EncodedMessage message;
    private DeviceOperator deviceOperator;


    public DecodeProcessor(DecodeContext context) {
        this.context = context;
        this.message = context.getMessage();
    }

    public DecodeResult process() {
        byte[] bytes = message.getPayload();
        Map<String, Object> commonProperties = new LinkedHashMap<>();
        int p = 0;
        // 头 0x68
        byte h = bytes[p++];
        // NB 前缀，不用关心，没啥用
        // 0-deviceReq（设备上报数据）
        // 1-deviceRsp（设备回复数据）
        // 2-serverReq（服务器主动下发指令数据）
        // 3-serverRsp（服务器回复数据）
        byte msgType = bytes[p++];
        // 是否存在后续帧，这个忽略掉
        byte hasMore = bytes[p++];
        //保留字段的附加信息标志
        byte extraInfo = bytes[p++];
        // 预留1字节
        p += 1;
        // 版本号
        byte ver = bytes[p++];
        // 协议版本号
        commonProperties.put("hasMore", hasMore);
        commonProperties.put("ver", ver);
        // 厂商代码
        byte mfrNo = bytes[p++];
        commonProperties.put("mfr_no", mfrNo);
        // 命令码，只是用来识别数据类型的，就不存了
        byte cmdType = bytes[p++];
        if (cmdType == 0) {
            log.info("忽略 0x00消息");
            return null;
        }
        // 加密标识，用来判断数据是否是加密的
        byte encrypt = bytes[p++];
        commonProperties.put("encrypt", encrypt);
        commonProperties.put("cmdType", String.format("%02x", cmdType));
        // 目标地址（表具编号）
        String iccid = ByteConvertUtil.bytes2Ascii(bytes, p, 20).trim();

        String deviceNo = ByteParseUtil.getDeviceNo(bytes);
        deviceOperator = context.getDevice(deviceNo);
        if (null == deviceOperator) {
            log.warnf("设备 %s 不存在", deviceNo);
            // 回复设备失败？
            byte[] replyBytes = ByteParseUtil.buildRespCommand(cmdType, ver, iccid, deviceNo, -3, false);
            this.sendToDevice(replyBytes);
            return null;
        }
        deviceOperator.setProperty("iccid", iccid);
        commonProperties.put("iccid", iccid);
        p += 20;
        // 数据域长度
        int dataLen = ByteConvertUtil.bytes2IntSmall(bytes, p, 2);
        p += 2;
        if (dataLen > bytes.length) {
            throw new MessageCodecException("数据域长度有误，当前数据域长度超过了整个报文长度");
        }
        // 数据域（长度 = dataLen - 30 -1）
        byte[] dataRegion = Arrays.copyOfRange(bytes, p, dataLen - 3);
        switch (cmdType) {
            case ByteParseUtil.OrderIdConstant.ONE_DATA_REPORT:
                DeviceMeterMessage deviceDataMessage = ByteParseUtil.parseOneDataReport(dataRegion, commonProperties);
                byte[] replyBytes = ByteParseUtil.buildRespCommand(cmdType, ver, iccid, deviceNo, 0, deviceOperator.getWaitCmdCount() > 0);
                this.sendToDevice(replyBytes);
                return new DefaultDecodeResult(deviceOperator.getDeviceId(), Arrays.asList(deviceDataMessage));
            case ByteParseUtil.OrderIdConstant.ALARM_REPORT:
                replyBytes = ByteParseUtil.buildRespCommand(cmdType, ver, iccid, deviceNo, 0, deviceOperator.getWaitCmdCount() > 0);
                this.sendToDevice(replyBytes);
                return new DefaultDecodeResult(deviceOperator.getDeviceId(), Arrays.asList(ByteParseUtil.parseAlarm(dataRegion, commonProperties)));
            case ByteParseUtil.OrderIdConstant.VALVE_RESP:
                DeviceResponseMessage responseMessage = ByteParseUtil.parseValveResp(dataRegion, commonProperties);
                deviceOperator.setProperty("valve_state", responseMessage.getProperty("valve_state"));
                return new DefaultResponseResult("", "VALVE_RESP", responseMessage);
        }
        return null;
    }

    public void sendToDevice(byte[] replyBytes) {
        context.send(new TcpMessage(replyBytes));
    }

}
