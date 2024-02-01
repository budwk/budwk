package com.budwk.app.device.storage.objects.dto;

import lombok.Data;

/**
 * 原生预警信息
 *
 * */
@Data
public class NativeWarningInfoDTO {

    /**
     * 预警类型
     */
    private String warningType;

    /**
     * 预警类型名称
     */
    private String warningTypeName;

    /**
     * 预警时间
     */
    private Long warningTime;
    /**
     * 告警值
     */
    private String warningValue;
}
