package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_log;
import com.budwk.nb.sys.services.SysLogService;
import org.apache.commons.lang3.math.NumberUtils;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wizzer on 2016/12/22.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysLogService.class)
public class SysLogServiceImpl extends BaseServiceImpl<Sys_log> implements SysLogService {
    public SysLogServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 按月分表的dao实例
     */
    protected Map<String, Dao> ymDaos = new HashMap<String, Dao>();

    /**
     * 获取按月分表的Dao实例,即当前日期的dao实例
     *
     * @return
     */
    public Dao logDao() {
        Calendar cal = Calendar.getInstance();
        String key = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        return logDao(key);
    }

    /**
     * 获取特定月份的Dao实例
     *
     * @param key
     * @return
     */
    public Dao logDao(String key) {
        Dao dao = ymDaos.get(key);
        if (dao == null) {
            synchronized (this) {
                dao = ymDaos.get(key);
                if (dao == null) {
                    dao = Daos.ext(this.dao(), key);
                    dao.create(Sys_log.class, false);
                    ymDaos.put(key, dao);
                    try {
                        Daos.migration(dao, Sys_log.class, true, false);
                    } catch (Throwable e) {
                    }
                }
            }
        }
        return dao;
    }

    @Async
    public void fastInsertSysLog(Sys_log syslog) {
        logDao().insert(syslog);
    }


    public Pagination list(String type, String loginname, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize) {
        String tableName = Times.format("yyyyMM", new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select sl.* from (");
        if (startTime == 0 && endTime == 0) {
            stringBuilder.append(" select * from sys_log_" + tableName);
            if (Strings.isNotBlank(type)) {
                stringBuilder.append(" where type='" + type + "'");
            }
        } else {
            int m1 = NumberUtils.toInt(Times.format("yyyyMM", Times.D(startTime)));
            int m2 = NumberUtils.toInt(Times.format("yyyyMM", Times.D(endTime)));
            if (m1 == m2) {
                stringBuilder.append(" select * from sys_log_" + m1 + " where 1=1 ");
                if (Strings.isNotBlank(type)) {
                    stringBuilder.append(" and type='" + type + "'");
                }
                stringBuilder.append(" and createdAt>=" + startTime);
                stringBuilder.append(" and createdAt<=" + endTime);
            } else {
                for (int i = m1; i < m2 + 1; i++) {
                    if (this.dao().exists("sys_log_" + i)) {
                        stringBuilder.append(" select * from sys_log_" + i + " where 1=1 ");
                        if (Strings.isNotBlank(type)) {
                            stringBuilder.append(" and type='" + type + "'");
                        }
                        stringBuilder.append(" and createdAt>=" + startTime);
                        stringBuilder.append(" and createdAt<=" + endTime);
                        if (i < m2) {
                            stringBuilder.append(" UNION ALL ");
                        }
                    }
                }
            }
        }
        stringBuilder.append(")sl ");
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            stringBuilder.append(" order by sl." + pageOrderName + " " + pageOrderBy);
        }
        return this.listPage(pageNumber, pageSize, Sqls.create(stringBuilder.toString()));
    }

}
