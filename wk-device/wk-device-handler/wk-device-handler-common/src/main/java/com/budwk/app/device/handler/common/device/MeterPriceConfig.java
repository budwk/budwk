package com.budwk.app.device.handler.common.device;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表具价格信息
 *
 */
@Data
public class MeterPriceConfig {
    /**
     * 配置编号
     */
    private String configId;
    /**
     * 周期类型，0 月阶梯，1 年阶梯
     */
    private int periodType;
    /**
     * 周期长度  当值为 1 ，且周期类型为 0（年） 时代表 1 年，周期类型为 1（月）时代表 1 月
     */
    private int periodLen;
    /**
     * 生效时间
     */
    private Long effectiveTime;
    /**
     * 阶梯起始读数，部分表具支持设置。默认取最新读数
     */
    private Double startReadNumber;

    /**
     * 阶梯价格信息
     */
    private List<StepPrice> steps;

    public MeterPriceConfig addStep(long price, Double volume) {
        if (null == steps) {
            steps = new ArrayList<>();
        }
        steps.add(new StepPrice(price, volume));
        return this;
    }


    @Data
    public static class StepPrice {
        /**
         * 价格，单位分
         */
        private final Long price;
        /**
         * 用量
         */
        private final Double volume;
    }
}
