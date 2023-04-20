package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.container.TableScheme;
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
    void save(DeviceDataDTO device, Map<String, Object> dataList);

    List<NutMap> list(DeviceDataQuery query);

    long count(DeviceDataQuery query);

    void createTable(TableScheme scheme);
}
