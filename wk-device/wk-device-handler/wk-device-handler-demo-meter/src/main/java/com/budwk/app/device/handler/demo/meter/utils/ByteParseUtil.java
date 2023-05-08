package com.budwk.app.device.handler.demo.meter.utils;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.exception.MessageCodecException;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.nio.ByteBuffer;

/**
 * @author wizzer.cn
 */
public class ByteParseUtil {
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
        COMM_PARAM_SET,
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
        public static final byte ONE_DATA_PUSH = 0x07;
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
        /**
         * 通讯参数设置
         */
        public static final byte COMM_PARAM_SET = 0x15;
        /**
         * 回复通讯参数设置/时间参数设置
         */
        public static final byte COMM_PARAM_RESP = 0x16;
    }
}
