package com.budwk.app.device.handler.demo.meter.codec;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.context.DecodeContext;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;
import com.budwk.app.device.handler.common.message.codec.TcpMessage;

/**
 * @author wizzer.cn
 */
public class DecodeProcessor {
    private final DecodeContext context;
    private final EncodedMessage message;
    private DeviceOperator deviceOperator;


    public DecodeProcessor(DecodeContext context) {
        this.context = context;
        this.message = context.getMessage();
    }

    public DecodeResult process() {
        byte[] bytes = message.getPayload();
        return null;
    }

    public void sendToDevice(byte[] replyBytes) {
        context.send(new TcpMessage(replyBytes));
    }

}
