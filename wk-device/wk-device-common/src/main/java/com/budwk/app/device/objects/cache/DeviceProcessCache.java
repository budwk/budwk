package com.budwk.app.device.objects.cache;

import com.budwk.app.device.enums.DeviceType;
import com.budwk.app.device.enums.ValveState;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceProcessCache implements Serializable {
    private static final long serialVersionUID = -6811997276897409344L;
    // 产品ID
    private String productId;
    // 业务类型
    private DeviceType deviceType;
    //集中器ID
    //private String collectorId;
    // 设备ID
    private String deviceId;
    // 设备通信号
    private String deviceNo;
    // 设备iccid
    private String iccid;
    private String supplierId;
    // 上次抄表时间
    private Long lastReadTime;
    // 上次表具读数
    private Double lastReadNumber;
    // 上次接收时间
    private Long lastReceiveTime;
    // 阀门状态
    private ValveState lastValveState;
}
