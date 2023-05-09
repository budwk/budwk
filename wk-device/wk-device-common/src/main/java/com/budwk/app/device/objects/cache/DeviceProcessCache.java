package com.budwk.app.device.objects.cache;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceProcessCache implements Serializable {
    private static final long serialVersionUID = -6811997276897409344L;
    private String productId;
    //集中器ID
    private String collectorId;
    private String deviceId;
    private String deviceNo;
    private String deviceCode;
    private String supplierId;
    private Integer valveState;
    //表具读数
    private Double readNumber;
}
