package com.budwk.app.device.handler.demo.meter.utils;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.exception.MessageCodecException;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.device.ValueItem;
import com.budwk.app.device.handler.common.enums.ValveState;
import com.budwk.app.device.handler.common.message.DeviceEventMessage;
import com.budwk.app.device.handler.common.message.DeviceMeterMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import com.budwk.app.device.handler.demo.meter.enums.AlarmType;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author wizzer.cn
 */
public class ByteParseUtil {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    /**
     * 获取设备号
     *
     * @param bytes
     * @return
     */
    public static String getDeviceNo(byte[] bytes) {
        return ByteConvertUtil.bytes2Ascii(bytes, 31, 16).trim();
    }

    /**
     * 单条数据上报解析
     */
    public static DeviceMeterMessage parseOneDataReport(byte[] dataRegion, Map<String, Object> commonProperties) {
        DeviceMeterMessage message = new DeviceMeterMessage();
        message.setMessageId("one_data_report");
        message.setTimestamp(System.currentTimeMillis());
        message.getProperties().putAll(commonProperties);
        int p = 0;
        // 表具通信号
        String deviceNo = ByteConvertUtil.bytes2Ascii(dataRegion, p, 16);
        message.addProperty("meter_no", deviceNo.trim());
        p += 16;
        // 上报时间(HHmm)
        String reportTime = ByteConvertUtil.bcb2String(dataRegion, p, 2);
        message.addProperty("report_time", reportTime);
        p += 2;
        // 抄表时间(yyMMddhhmmss)
        String deviceTime = ByteConvertUtil.bytes2String(dataRegion, p, 6);
        long meterTimeMs = LocalDateTime.parse(deviceTime, TIME_FORMAT).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L;
        message.setReadingTime(meterTimeMs);
        p += 6;
        // 标况总量
        int standardNum = ByteConvertUtil.bytes2IntSmall(dataRegion, p, 4);
        double value = BigDecimal.valueOf(standardNum).divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP).doubleValue();
        message.addProperty("report_reading", value);
        message.setReadingNumber(value);
        int valveState = dataRegion[p++];
        message.setValveState(ValveState.from(valveState));
        message.addProperty("reading_time", message.getReadingTime());
        message.addProperty("valve_state", valveState);
        return message;
    }

    /**
     * 报警上报
     */
    public static DeviceEventMessage parseAlarm(byte[] dataRegion, Map<String, Object> commonProperties) {
        int p = 0;
        // 表具通信号
        String deviceNo = ByteConvertUtil.bytes2Ascii(dataRegion, p, 16);
        p += 16;
        // 报警时间(yyMMddhhmmss)
        String deviceTime = ByteConvertUtil.bytes2String(dataRegion, p, 6);
        p += 6;
        // 报警位 0-报警恢复 1-报警
        byte alarmState = dataRegion[p++];
        // 报警类型
        AlarmType alarmType = AlarmType.from(dataRegion[p++]);
        DeviceEventMessage eventMessage = new DeviceEventMessage();
        eventMessage.setEventType(String.valueOf(alarmType.value()));
        eventMessage.setEventName(String.valueOf(alarmType.text()));
        eventMessage.setType(alarmState == 0 ? DeviceEventMessage.Type.ALARM_RECOVER : DeviceEventMessage.Type.ALARM);
        eventMessage.setTimestamp(LocalDateTime.parse(deviceTime, TIME_FORMAT).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L);
        eventMessage.setContent(alarmType.text() + (alarmState == 0 ? "恢复" : ""));
        // 0-无阀门
        // 1-阀门开
        // 2-阀门临时关(用户可手动打开)
        // 3-阀门强制关(用户无法手动打开)
        // 4-阀门状态未知
        // 5-阀门异常
        int alarmValveState = dataRegion[p++];
        String prefix = "报警" + (alarmState == 0 ? "恢复" : "") + "时";
        eventMessage.addProperty("valve_state", prefix + "阀门状态", ValveState.from(alarmValveState).text());
        ValueItem<? extends Serializable> dto = eventMessage.getProperties().stream().filter(it -> it.getCode().equalsIgnoreCase(alarmType.propKey())).findFirst().orElse(null);
        eventMessage.setWarningValue(dto == null ? "" : Strings.sBlank(dto.getValue()));
        return eventMessage;
    }

    /**
     * 回复阀门控制
     */
    public static DeviceResponseMessage parseValveResp(byte[] dataRegion, Map<String, Object> commonProperties) {
        int p = 0;
        p += 16;
        // 回复结果 1-成功
        byte respResult = dataRegion[p++];
        // 0-无阀门
        // 1-阀门开
        // 2-阀门临时关(用户可手动打开)
        // 3-阀门强制关(用户无法手动打开)
        // 4-阀门状态未知
        // 5-阀门异常
        int valveState = dataRegion[p];
        DeviceResponseMessage message = new DeviceResponseMessage();
        message.setSuccess(respResult == 1);
        message.setCommandCode(DeviceCommand.VALVE_CONTROL.name());
        message.addProperty("valve_state", valveState);
        return message;
    }

    /**
     * 生成指令
     *
     * @param command
     * @param device
     * @return
     */
    public static byte[] buildCommand(CommandInfo command, DeviceOperator device) {
        if (command == null) {
            throw new MessageCodecException("构造指令的参数不能为空");
        }
        boolean hasMore = device.getWaitCmdCount() > 1;
        String method = Strings.sBlank(command.getCommandCode());
        DeviceCommand deviceCommand = DeviceCommand.from(method);
        byte[] dataBytes;
        byte orderId;
        if (null == deviceCommand) {
            throw new MessageCodecException("暂不支持的指令");
        }
        String deviceNo = Strings.sBlank(device.getProperty("deviceNo"));
        NutMap params = Lang.obj2nutmap(command.getParams());
        switch (deviceCommand) {
            case VALVE_CONTROL:
                orderId = OrderIdConstant.VALVE_CONTROL;
                dataBytes = valveControl(deviceNo, params);
                break;
            case END:
                hasMore = false;
            default:
                throw new RuntimeException("暂不支持的指令");
        }
        return fillFrame(orderId, (byte) 1, deviceNo, dataBytes, true, hasMore);
    }

    /**
     * 组装协议数据
     *
     * @param cmdType   命令码
     * @param ver       协议版本号
     * @param iccid     iccid
     * @param dataRegin 数据区域
     * @param active    是否主动发送
     * @param hasMore   是否有下一条
     * @return
     */
    public static byte[] fillFrame(byte cmdType, byte ver, String iccid, byte[] dataRegin, boolean active, boolean hasMore) {
        int dataLen = dataRegin.length + 34;
        ByteBuffer buffer = ByteBuffer.allocate(dataLen);
        buffer.put((byte) 0x68);
        if (active) {
            buffer.put((byte) 0x02);
        } else {
            buffer.put((byte) 0x03);
        }
        if (hasMore) {
            buffer.put((byte) 0x01);
        } else {
            buffer.put((byte) 0x00);
        }
        buffer.put(new byte[2]);
        buffer.put(ver);
        buffer.put((byte) 0x00);
        buffer.put(cmdType);
        buffer.put((byte) 0x00);
        buffer.put(ByteConvertUtil.ascii2Bytes(Strings.alignLeft(iccid, 20, '0')));
        ByteConvertUtil.putBufferBytesReverse(buffer, ByteConvertUtil.getBytes((short) dataLen));
        buffer.put(dataRegin);
        byte[] bytes = buffer.array();
        // crc16校验
        int crc = ByteConvertUtil.crc16(bytes, 0, dataLen - 3);
        byte[] crcBytes = ByteConvertUtil.getBytes(crc);
        bytes[bytes.length - 3] = crcBytes[3];
        bytes[bytes.length - 2] = crcBytes[2];
        bytes[bytes.length - 1] = 0x16;
        return bytes;
    }

    /**
     * 参数格式
     * {"status":0},0-3分别表示开阀、关阀、强制关阀
     *
     * @param deviceNo
     * @param nutMap   参数
     * @return
     */
    private static byte[] valveControl(String deviceNo, NutMap nutMap) {
        ByteBuffer buffer = ByteBuffer.allocate(18);
        buffer.put(ByteConvertUtil.ascii2Bytes(Strings.alignRight(nutMap.getString("meter_no", deviceNo), 16, ' ')));
        int status = nutMap.getInt("status", -1);
        switch (status) {
            case 0:
                buffer.put(ByteConvertUtil.hexToBytes("33CC"));
                break;
            case 1:
                buffer.put(ByteConvertUtil.hexToBytes("34CB"));
                break;
            case 2:
                buffer.put(ByteConvertUtil.hexToBytes("35CA"));
                break;
            default:
                throw new MessageCodecException("阀控指令参数错误");

        }
        return buffer.array();
    }

    /**
     * 自动回复
     *
     * @param respCode 错误码(1=正常)
     * @param hasMore  是否有下一条指令
     */
    public static byte[] buildRespCommand(byte cmdType,
                                          byte ver,
                                          String iccid,
                                          String deviceNo,
                                          int respCode,
                                          boolean hasMore) {

        byte[] dataBytes;
        byte orderId;
        boolean active = false;

        switch (cmdType) {
            case OrderIdConstant.ONE_DATA_REPORT:
                orderId = OrderIdConstant.ONE_DATA_RESP;
                dataBytes = oneDataResp(deviceNo, respCode);
                break;
            case OrderIdConstant.ALARM_REPORT:
                orderId = OrderIdConstant.ALARM_RESP;
                dataBytes = alarmResp(deviceNo, respCode);
                break;
            default:
                throw new RuntimeException("暂不支持的指令");
        }
        return fillFrame(orderId, ver, iccid, dataBytes, active, hasMore);
    }


    private static byte[] oneDataResp(String deviceNo, int respCode) {
        ByteBuffer buffer = ByteBuffer.allocate(23);
        buffer.put(ByteConvertUtil.ascii2Bytes(Strings.alignRight(deviceNo, 16, ' ')));
        buffer.put((byte) respCode);
        buffer.put(ByteConvertUtil.strToBcd(Times.format("yyMMddHHmmss", new Date())));
        return buffer.array();
    }

    private static byte[] alarmResp(String deviceNo, int respCode) {
        ByteBuffer buffer = ByteBuffer.allocate(23);
        buffer.put(ByteConvertUtil.ascii2Bytes(Strings.alignRight(deviceNo, 16, ' ')));
        buffer.put((byte) respCode);
        buffer.put(ByteConvertUtil.strToBcd(Times.format("yyMMddHHmmss", new Date())));
        return buffer.array();
    }

    /**
     * 当前以实现的设备指令
     */
    public enum DeviceCommand {
        /**
         * 回复单条数据上报
         */
        ONE_DATA_RESP,
        /**
         * 回复报警数据
         */
        ALARM_RESP,
        /**
         * 阀门控制
         */
        VALVE_CONTROL,
        /**
         * 通讯参数设置
         */
        END;

        public static DeviceCommand from(String name) {
            try {
                return DeviceCommand.valueOf(name);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class OrderIdConstant {
        /**
         * 单条数据上报
         */
        public static final byte ONE_DATA_REPORT = 0x07;
        /**
         * 回复单条数据上报
         */
        public static final byte ONE_DATA_RESP = 0x8;
        /**
         * 报警上报
         */
        public static final byte ALARM_REPORT = 0xD;
        /**
         * 回复报警上报
         */
        public static final byte ALARM_RESP = 0xE;
        /**
         * 阀门控制
         */
        public static final byte VALVE_CONTROL = 0xF;
        /**
         * 回复阀门控制
         */
        public static final byte VALVE_RESP = 0x10;
    }
}
