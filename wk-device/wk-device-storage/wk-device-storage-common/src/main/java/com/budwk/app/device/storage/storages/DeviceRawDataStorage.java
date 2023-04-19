package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.dto.DeviceRawDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceRawDataQuery;

import java.util.List;

/**
 * 原始报文存取
 * @author wizzer.cn
 */
public interface DeviceRawDataStorage {

    void save(DeviceRawDataDTO data);

    List<DeviceRawDataDTO> list(DeviceRawDataQuery query);

    long count(DeviceRawDataQuery query);
}
