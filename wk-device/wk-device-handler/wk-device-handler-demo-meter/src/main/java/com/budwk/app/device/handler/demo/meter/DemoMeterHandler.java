package com.budwk.app.device.handler.demo.meter;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.Handler;
import com.budwk.app.device.handler.common.codec.MessageCodec;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.handler.common.enums.TransportType;
import com.budwk.app.device.handler.common.utils.aep.AepPlatformHelper;
import com.budwk.app.device.handler.demo.meter.codec.DefaultMessageCodec;
import com.budwk.app.device.handler.demo.meter.codec.HttpMessageCodec;
import org.nutz.lang.Strings;

/**
 * 演示表具协议解析(支持TCP/UDP 和 AEP平台HTTP)
 *
 * @author wizzer.cn
 */
public class DemoMeterHandler implements Handler {
    public static final String HANDLER_CODE = "DEMO_METER";
    public static final String HANDLER_NAME = "演示表具协议";

    @Override
    public String getCode() {
        return HANDLER_CODE;
    }

    @Override
    public String getName() {
        return HANDLER_NAME;
    }

    @Override
    public MessageCodec getMessageCodec(TransportType transportType) {
        switch (transportType) {
            case TCP:
            case UDP:
                return new DefaultMessageCodec();
            case HTTP:
                return new HttpMessageCodec();
        }
        return null;
    }

    @Override
    public void onDeviceRegistered(DeviceContext context, DeviceOperator deviceOperator) {
        ProductInfo product = deviceOperator.getProduct();
        if (null == product || !TransportType.HTTP.name().equalsIgnoreCase(product.getTransportType())) {
            return;
        }
        // AEP平台
        if ("AEP".equalsIgnoreCase(product.getPlatform())) {
            AepPlatformHelper aepPlatformHelper = new AepPlatformHelper(product.getFileConfig());
            String platformDeviceId = aepPlatformHelper.registerDevice(deviceOperator);
            if (Strings.isNotBlank(platformDeviceId)) {
                context.update(deviceOperator.getDeviceId(), "platformDeviceId", platformDeviceId);
            }
        }
    }

    @Override
    public void onDeviceUnRegistered(DeviceContext context, DeviceOperator deviceOperator) {
        ProductInfo product = deviceOperator.getProduct();
        if (null == product || !TransportType.HTTP.name().equalsIgnoreCase(product.getTransportType())) {
            return;
        }
        String platformDeviceId = Strings.sBlank(deviceOperator.getProperty("platformDeviceId"));
        // AEP平台
        if ("AEP".equalsIgnoreCase(product.getPlatform())) {
            AepPlatformHelper aepPlatformHelper = new AepPlatformHelper(product.getFileConfig());
            aepPlatformHelper.deleteDevice(product.getAuthConfig().get("masterKey"), product.getAuthConfig().get("productId"), platformDeviceId);
        }
    }
}
