package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.container.TableScheme;
import com.budwk.app.device.storage.objects.dto.DeviceDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceDataQuery;
import com.budwk.starter.tdengine.service.TDEngineServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:tdengineDao"})
public class TDEngineDeviceDataStorageImpl extends TDEngineServiceImpl<DeviceDataDTO> implements DeviceDataStorage {

    public TDEngineDeviceDataStorageImpl(Dao dao) {
        super(dao);
    }

    private final static String TABLE_PREFIX = "device_data_";

    @Override
    public void save(DeviceDataDTO dataDTO, Map<String, Object> dataList) {
        String tableName = String.format("%s_%s", TABLE_PREFIX, dataDTO.getHandler());
        String subTableName = String.format("%s_%s", TABLE_PREFIX, dataDTO.getDevice_id());
        NutMap data = Lang.obj2nutmap(dataDTO);
        dataList.forEach((k, v) -> {
            data.addv(k, v);
        });
        this.insert(tableName, subTableName, data);
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
