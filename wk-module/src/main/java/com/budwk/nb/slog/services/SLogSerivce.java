package com.budwk.nb.slog.services;

import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.sys.models.Sys_log;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/13
 */
public interface SLogSerivce {
    /**
     * 创建日志
     *
     * @param syslog 日志对象
     */
    void create(Sys_log syslog);

    /**
     * 分页查询日志
     *
     * @param type          日志类型
     * @param loginname     登陆名
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
