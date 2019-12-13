package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_log;
import com.budwk.nb.commons.base.service.BaseService;
import com.budwk.nb.commons.base.page.Pagination;
import org.nutz.dao.Cnd;

/**
 * Created by wizzer on 2016/12/22.
 */
public interface SysLogService extends BaseService<Sys_log> {
    /**
     * 快速插入日志
     *
     * @param syslog
     */
    void fastInsertSysLog(Sys_log syslog);
    /**
     * 查询日期
     *
     * @param tablaeName 分表名称
     * @param pageNumber 页码
     * @param pageSize   页大小
     * @param cnd        查询条件
     * @return
     */
    Pagination data(String tablaeName, int pageNumber, int pageSize, Cnd cnd);

    /**
     * 多月日志条件查询
     *
     * @param date          时间范围
     * @param type          日志类型
     * @param pageOrderName 排序字段名称
     * @param pageOrderBy   排序方式
     * @param pageNumber    页码
     * @param pageSize      页大小
     * @return
     */
    Pagination data(String[] date, String type, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize);
}
