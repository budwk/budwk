package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.dto.DeviceDTO;
import com.budwk.app.device.storage.objects.dto.EventInfoDTO;

import java.util.List;

/**
 * 事件数据存储
 */
public interface DeviceEventStorage {
    void saveEvent(DeviceDTO device, List<EventInfoDTO> eventList);
}
