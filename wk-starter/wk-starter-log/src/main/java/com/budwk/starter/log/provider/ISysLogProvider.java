package com.budwk.starter.log.provider;

import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.model.Sys_log;

/**
 * @author wizzer@qq.com
 */
public interface ISysLogProvider {

    /**
     * 保存日志
     *
     * @param sysLog 日志对象
     */
    void saveLog(Sys_log sysLog);

    /**
     * 查询日志
     *
     * @param status        操作状态
     * @param type          日志类型
     * @param appId         应用ID
     * @param tag           日志标签
     * @param msg           日志内容
     * @param userId        用户ID
     * @param loginname     用户名
     * @param username      姓名或昵称
     * @param startTime     开始时间
     * @param endTime       截至时间
     * @param pageOrderName 排序字段
     * @param pageOrderBy   排序方式
     * @param pageNumber    分页页码
     * @param pageSize      分页大小
     * @return
     */
    Pagination list(String status, LogType type, String appId, String tag, String msg, String userId, String loginname, String username, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize);
}
