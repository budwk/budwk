package com.budwk.app.device.handler.demo.meter.codec;

import com.budwk.app.device.handler.common.codec.MessageCodec;
import com.budwk.app.device.handler.common.codec.context.DecodeContext;
import com.budwk.app.device.handler.common.codec.context.EncodeContext;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.codec.result.EncodeResult;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.message.codec.TcpMessage;
import com.budwk.app.device.handler.demo.meter.DemoMeterHandler;
import com.budwk.app.device.handler.demo.meter.utils.ByteParseUtil;

import java.util.Arrays;

/**
 * @author wizzer.cn
 */
public class DefaultMessageCodec implements MessageCodec {
    @Override
    public DecodeResult decode(DecodeContext context) {
        return new DecodeProcessor(context).process();
    }

    @Override
    public EncodeResult encode(EncodeContext context) {
        CommandInfo commandInfo = context.getCommandInfo();
        byte[] bytes = ByteParseUtil.buildCommand(commandInfo, context.getDeviceOperator());
        TcpMessage tcpMessage = new TcpMessage(bytes);
        return EncodeResult.createDefault(false, Arrays.asList(tcpMessage));
    }
}
