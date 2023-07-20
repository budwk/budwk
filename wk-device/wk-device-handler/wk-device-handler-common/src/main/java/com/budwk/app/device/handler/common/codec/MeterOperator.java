package com.budwk.app.device.handler.common.codec;

import com.budwk.app.device.handler.common.device.MeterBillingInfo;

/**
 * 表具业务操作
 * @author wizzer.cn
 */
public interface MeterOperator extends DeviceOperator{
    MeterBillingInfo getMeterBillingInfo();
}
