package com.budwk.app.device.handler.demo.meter;

import com.budwk.app.device.handler.common.codec.Handler;
import com.budwk.app.device.handler.common.codec.MessageCodec;
import com.budwk.app.device.handler.common.enums.TransportType;

/**
 * @author wizzer.cn
 */
public class DemoMeterHandler implements Handler {
    @Override
    public String getCode() {
        return "DEMO_METER";
    }

    @Override
    public String getName() {
        return "演示表具协议";
    }

    @Override
    public MessageCodec getMessageCodec(TransportType transportType) {
        switch (transportType) {
            case TCP:
            case UDP:
            default:
        }
        return null;
    }
}
