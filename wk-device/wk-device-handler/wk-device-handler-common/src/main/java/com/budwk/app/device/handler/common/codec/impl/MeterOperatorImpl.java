package com.budwk.app.device.handler.common.codec.impl;

import com.budwk.app.device.handler.common.codec.MeterOperator;
import com.budwk.app.device.handler.common.device.MeterBillingInfo;
import lombok.Data;

/**
 * @author wizzer.cn
 */
@Data
public class MeterOperatorImpl extends DefaultDeviceOperator implements MeterOperator {

    /**
     * 计费信息
     */
    private MeterBillingInfo meterBillingInfo;
}
