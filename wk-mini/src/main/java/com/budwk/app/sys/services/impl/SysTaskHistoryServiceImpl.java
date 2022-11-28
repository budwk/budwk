package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.models.Sys_task_history;
import com.budwk.app.sys.services.SysTaskHistoryService;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysTaskHistoryServiceImpl extends BaseServiceImpl<Sys_task_history> implements SysTaskHistoryService {
    public SysTaskHistoryServiceImpl(Dao dao) {
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
                    dao.create(Sys_task_history.class, false);
                    ymDaos.put(key, dao);
                }
            }
        }
        return dao;
    }

    @Override
    public void save(Sys_task_history sysTaskHistory) {
        logDao().insert(sysTaskHistory);
    }

    @Override
    public Pagination getList(String month, int pageNo, int pageSize, Condition cnd) {
        String tableName = Strings.isBlank(month) ? Times.format("yyyyMM", new Date()) : month;
        if (!this.dao().exists("sys_task_history_" + tableName)) {
            throw new BaseException(tableName + " 表不存在");
        }
        return this.listPage(logDao(tableName), pageNo, pageSize, cnd);
    }
}
