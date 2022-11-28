package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_task_history;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseService;
import org.nutz.dao.Condition;

/**
 * @author wizzer@qq.com
 */
public interface SysTaskHistoryService extends BaseService<Sys_task_history> {
    void save(Sys_task_history sysTaskHistory);
    Pagination getList(String month, int pageNo, int pageSize, Condition cnd);
}
