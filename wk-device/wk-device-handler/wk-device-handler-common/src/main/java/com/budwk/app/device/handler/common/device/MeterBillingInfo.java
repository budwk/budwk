package com.budwk.app.device.handler.common.device;

import lombok.Data;

import java.io.Serializable;

/**
 * 表具账户信息
 */
@Data
public class MeterBillingInfo implements Serializable {

    private static final long serialVersionUID = 3505492043929459822L;
    /**
     * 用户账号
     */
    private String accountNo;
    /**
     * 余额
     */
    private Long balance;
    /**
     * 总用量，即当前表具使用以来所有用量
     */
    private Double totalVolume;
    /**
     * 阶梯用量，即当前阶梯内已使用的用量
     */
    private Double ladderVolume;
    /**
     * 当前阶梯价格
     */
    private String ladderPrice;
    /**
     * 当前所处阶梯
     */
    private Integer currentLadder;
    /**
     * 上次读数
     */
    private Double lastReadingNumber;
    /**
     * 上次抄表时间
     */
    private Long lastReadingTime;
    /**
     * 当前用量。即本次上报后的用量。需要注意，这个值第一次获取时是null，需要调用者设置才会有值
     */
    private Double currentVolume;
    /**
     * 计费信息，包含了价格配置
     */
    private MeterChargeInfo chargeInfo;

}
