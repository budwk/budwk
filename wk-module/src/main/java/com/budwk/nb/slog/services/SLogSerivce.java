package com.budwk.nb.slog.services;

import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.sys.models.Sys_log;

/**
 * Created by wizzer.cn on 2019/12/13
 */
public interface SLogSerivce {
    void create(Sys_log syslog);

    Pagination list(String type, String loginname, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize);
}
