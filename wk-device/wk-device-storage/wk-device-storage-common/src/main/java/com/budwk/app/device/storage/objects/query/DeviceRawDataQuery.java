package com.budwk.app.device.storage.objects.query;

import lombok.Data;

/**
 * 原始报文查询
 * @author wizzer.cn
 */
@Data
public class DeviceRawDataQuery extends PageQuery{
    private static final long serialVersionUID = 1L;

    //查询表名
    private String table;

    //设备id
    private String deviceId;

    //开始时间戳
    private Long startTime;

    //结束时间戳
    private Long endTime;

    //ids
    private String ids;
}
