package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_info_latest_data;
import com.budwk.app.device.services.DeviceInfoLatestDataService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceInfoLatestDataServiceImpl extends BaseServiceImpl<Device_info_latest_data> implements DeviceInfoLatestDataService {
    public DeviceInfoLatestDataServiceImpl(Dao dao) {
        super(dao);
    }
}