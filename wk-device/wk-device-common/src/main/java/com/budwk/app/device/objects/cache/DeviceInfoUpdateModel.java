package com.budwk.app.device.objects.cache;

import com.budwk.app.device.enums.DeviceValveState;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新缓存部分字段
 *
 * @author wizzer.cn
 */
@Data
public class DeviceInfoUpdateModel implements Serializable {
    private static final long serialVersionUID = 8909735572894087377L;
    // 上次抄表时间
    private Long lastReadTime;
    // 上次表具读数
    private Double lastReadNumber;
    // 上次接收时间
    private Long lastReceiveTime;
    // 阀门状态
    private DeviceValveState lastDeviceValveState;

}
