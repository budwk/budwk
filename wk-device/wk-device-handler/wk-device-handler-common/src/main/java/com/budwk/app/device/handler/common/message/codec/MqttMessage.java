package com.budwk.app.device.handler.common.message.codec;


import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import lombok.Data;

/**
 * @author wizzer.cn
 */
@Data
public class MqttMessage implements EncodedMessage {
    private static final long serialVersionUID = -5551990912154345413L;
    private final byte[] bytes;
    /**
     * 数据格式，默认是16进制，可选：hex 16进制（默认），string 字符串
     */
    private String payloadType = "hex";

    private String topic;

    private String clientId;

    private int qosLevel;

    private String messageId;

    private boolean will;

    private boolean dup;

    private boolean retain;


    @Override
    public byte[] getPayload() {
        return bytes;
    }

    @Override
    public String payloadAsString() {
        if ("hex".equals(payloadType)) {
            return ByteConvertUtil.bytesToHex(getPayload());
        }
        return EncodedMessage.super.payloadAsString();
    }

    @Override
    public String toString() {
        return "TcpMessage{" +
                "topic=" + topic +
                "clientId=" + clientId +
                "qosLevel=" + qosLevel +
                "messageId=" + messageId +
                "will=" + will +
                "dup=" + dup +
                "retain=" + retain +
                "bytes=" + payloadAsString() +
                '}';
    }
}
