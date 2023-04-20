package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.dto.DeviceAttributeDTO;
import com.budwk.app.device.storage.objects.dto.DeviceDTO;
import com.budwk.app.device.storage.objects.dto.DeviceDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceDataQuery;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * 设备上报数据存取
 *
 * @author wizzer.cn
 */
public interface DeviceDataStorage {
    void save(DeviceDataDTO deviceDataDTO, Map<String, Object> dataList);

    List<NutMap> list(DeviceDataQuery query);

    long count(DeviceDataQuery query);

    /**
     * 创建表结构(注意: 所有字段名为小写字母)
     * @param deviceDataDTO
     * @param attributeDTOS
     */
    void createTable(DeviceDataDTO deviceDataDTO, List<DeviceAttributeDTO> attributeDTOS);
}
