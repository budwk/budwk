package com.budwk.app.device.storage.objects.query;

import lombok.Data;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceDataQuery extends PageQuery{
    private static final long serialVersionUID = 1L;

    //解析器
    private String handler;

    //设备id
    private String deviceId;

    //开始时间戳
    private Long startTime;

    //结束时间戳
    private Long endTime;

    //ids
    private String ids;
}
