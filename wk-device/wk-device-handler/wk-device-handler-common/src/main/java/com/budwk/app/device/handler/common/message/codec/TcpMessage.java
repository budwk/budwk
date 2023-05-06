package com.budwk.app.device.handler.common.message.codec;


import com.budwk.app.device.handler.common.utils.ByteConvert;
import lombok.Data;

/**
 * @author wizzer.cn
 * @author zyang
 */
@Data
public class TcpMessage implements EncodedMessage {
    private static final long serialVersionUID = -5874333451596406751L;
    private final byte[] bytes;
    /**
     * 数据格式，默认是16进制，可选：hex 16进制（默认），string 字符串
     */
    private String payloadType = "hex";

    @Override
    public byte[] getPayload() {
        return bytes;
    }

    @Override
    public String payloadAsString() {
        if ("hex".equals(payloadType)) {
            return ByteConvert.bytesToHex(getPayload());
        }
        return EncodedMessage.super.payloadAsString();
    }

    @Override
    public String toString() {
        return "TcpMessage{" +
                "bytes=" + payloadAsString() +
                '}';
    }
}
