package com.budwk.app.device.objects.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AlarmNoticeDTO implements Serializable {

    /**
     * 设备id
     */
    private String deviceId;
    private String companyId;
    private String productId;
    /**
     * 告警记录id
     */
    private String warningId;
    /**
     * 类型
     */
    private String warningType;
    /**
     * 名称
     */
    private String waringName;
    /**
     * 用户名
     */
    private String accountName;
    /**
     * 设备编号
     */
    private String deviceNo;
    private String accountNo;
    /**
     * 告警级别
     */
    private String warningLevel;
    /**
     * 告警值
     */
    private String warningValue;
    /**
     * 告警时间
     */
    private Long warningTime;
    /**
     * 地址
     */
    private String address;

    private Map<String, Object> ext;
}
