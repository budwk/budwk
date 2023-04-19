package com.budwk.app.device.storage.objects.container;

import com.budwk.app.device.storage.enums.TimeSeriesGranularity;
import com.budwk.app.device.storage.objects.dto.DeviceAttributeDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer.cn
 */
@Data
public class TableScheme implements Serializable {

    private static final long serialVersionUID = -1L;

    //时序主键字段名称
    private String primaryKeyField;

    //数据时间间隔粒度
    private TimeSeriesGranularity granularity;

    //设备参数
    private List<DeviceAttributeDTO> attrs;

    //超级表名
    private String tableName;

    //子表名
    private String subTableName;
}
