package com.budwk.app.device.storage.objects.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String deviceNo;
    private String deviceCode;
    private String productId;
    private String handler;
}
