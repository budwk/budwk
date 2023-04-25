package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_type;
import com.budwk.app.device.services.DeviceTypeService;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
public class DeviceTypeServiceImpl extends BaseServiceImpl<Device_type> implements DeviceTypeService {
    public DeviceTypeServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void save(Device_type deviceType, String pid) {
        String path = "";
        if (Strings.isNotBlank(pid)) {
            Device_type pp = this.fetch(pid);
            path = pp.getPath();
        }
        deviceType.setPath(getSubPath("Device_type", "path", path));
        deviceType.setParentId(pid);
        dao().insert(deviceType);
        if (Strings.isNotBlank(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Device_type deviceType) {
        dao().execute(Sqls.create("delete from Device_type where path like @path").setParam("path", deviceType.getPath() + "%"));
        if (!Strings.isEmpty(deviceType.getParentId())) {
            int count = count(Cnd.where("parentId", "=", deviceType.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update Device_type set hasChildren=0 where id=@pid").setParam("pid", deviceType.getParentId()));
            }
        }
    }
}