package com.budwk.nb.sys.services;

import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.base.service.BaseService;
import com.budwk.nb.sys.models.Sys_log;

/**
 * @author wizzer(wizzer.cn) on 2016/12/22.
 */
public interface SysLogService extends BaseService<Sys_log> {
    /**
     * 快速插入日志
     *
     * @param syslog 日志对象
     */
    void fastInsertSysLog(Sys_log syslog);

    /**
     * 分页查询日志
     *
     * @param type          日志类型
     * @param loginname     用户名
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param pageOrderName 排序字段
     * @param pageOrderBy   排序方式
     * @param pageNumber    页码
     * @param pageSize      页大小
     * @return
     */
    Pagination list(String type, String loginname, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize);
}
