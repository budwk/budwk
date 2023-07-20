package com.budwk.app.device.services;


import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.objects.query.CommandQuery;
import com.budwk.starter.common.page.Pagination;

import java.util.List;

/**
 * 历史指令存mongodb
 */
public interface DeviceCommandRecordHistoryService {
    void save(Device_cmd_record cmdRecord);

    void save(List<Device_cmd_record> cmdRecordList);

    Pagination listPage(CommandQuery params);
}
