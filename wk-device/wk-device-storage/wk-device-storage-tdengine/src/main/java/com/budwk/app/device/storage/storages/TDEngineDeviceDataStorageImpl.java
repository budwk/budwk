package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.objects.dto.DeviceAttributeDTO;
import com.budwk.app.device.storage.objects.dto.DeviceDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceDataQuery;
import com.budwk.starter.tdengine.service.TDEngineServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
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

    public String getTableName(String handler) {
        return String.format("%s_%s", TABLE_PREFIX, handler);
    }

    public String getSubTableName(String deviceId) {
        return String.format("%s_%s", TABLE_PREFIX, deviceId);
    }

    @Override
    public void save(DeviceDataDTO dataDTO, Map<String, Object> dataList) {
        String tableName = getTableName(dataDTO.getHandler());
        String subTableName = getSubTableName(dataDTO.getDevice_id());
        NutMap data = Lang.obj2nutmap(dataDTO);
        dataList.forEach(data::addv);
        this.insert(tableName, subTableName, data);
    }

    @Override
    public List<NutMap> list(DeviceDataQuery query) {
        String tableName = getTableName(query.getHandler());
        if (Strings.isNotBlank(query.getDeviceId())) {
            tableName = getSubTableName(query.getDeviceId());
        }
        if (!exist(tableName)) {
            return new ArrayList<>();
        }
        return this.list(tableName, buildConditions(query), query.getPageNo(), query.getPageSize());

    }

    @Override
    public long count(DeviceDataQuery query) {
        String tableName = getTableName(query.getHandler());
        if (Strings.isNotBlank(query.getDeviceId())) {
            tableName = getSubTableName(query.getDeviceId());
        }
        if (!exist(tableName)) {
            return 0L;
        }
        return this.count(tableName, buildConditions(query));
    }

    @Override
    public void createTable(DeviceDataDTO deviceDataDTO, List<DeviceAttributeDTO> attributeDTOS) {
        String tableName = getTableName(deviceDataDTO.getHandler());
        List<NutMap> fieldsList = new ArrayList<>();
        // 将设备参数转化为map对象,以便dao层获取
        for (DeviceAttributeDTO dto : attributeDTOS) {
            fieldsList.add(NutMap.NEW().addv("field", dto.getCode())
                    .addv("length", dto.getDataLen())
                    .addv("type", dto.getDataTypeValue())
            );
        }
        this.createTable(tableName, DeviceDataDTO.class, fieldsList);
    }

    protected Condition buildConditions(DeviceDataQuery query) {
        Cnd cnd = Cnd.NEW();
        // 无需加入 device_id 条件, 因为是通过子表查询的(子表后缀名就是device_id)
        if (null != query.getStartTime()) {
            cnd.and("ts", ">=", query.getStartTime());
        }
        if (null != query.getEndTime()) {
            cnd.and("ts", ">=", query.getEndTime());
        }
        cnd.orderBy("ts", "desc");
        return cnd;
    }
}
