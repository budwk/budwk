package com.budwk.app.device.handler.common.device;

import lombok.Data;

/**
 * 表具计费信息
 */
@Data
public class MeterChargeInfo {
    /**
     * 计费方式：0 表端计费，1 平台计费
     */
    private int payMode;
    /**
     * 结算模式 0 预付费，1 后付费
     */
    private int settleMode;
    /**
     * 预付费结算方式，0 金额结算，1 气量结算
     */
    private int settlementType;
    /**
     * 阶梯价
     */
    private MeterPriceConfig priceConfigs;

}
