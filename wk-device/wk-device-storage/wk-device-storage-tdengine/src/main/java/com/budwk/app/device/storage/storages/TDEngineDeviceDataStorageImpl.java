package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.container.TableScheme;
import com.budwk.app.device.storage.objects.dto.DeviceDTO;
import com.budwk.app.device.storage.objects.dto.DeviceDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceDataQuery;
import com.budwk.starter.tdengine.service.TDEngineServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:taos"})
public class TDEngineDeviceDataStorageImpl extends TDEngineServiceImpl<DeviceDataDTO> implements DeviceDataStorage {

    public TDEngineDeviceDataStorageImpl(Dao dao) {
        super(dao);
    }

    @Override
    public void save(DeviceDTO device, long timestamp, Map<String, Object> dataList) {

    }

    @Override
    public List<NutMap> list(DeviceDataQuery query) {
        return null;
    }

    @Override
    public long count(DeviceDataQuery query) {
        return 0;
    }

    @Override
    public void createTable(TableScheme scheme) {

    }
}
