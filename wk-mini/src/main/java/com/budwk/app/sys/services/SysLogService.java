package com.budwk.app.sys.services;


import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseService;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.model.Sys_log;
import org.nutz.dao.Dao;

/**
 * @author wizzer@qq.com
 */
public interface SysLogService extends BaseService<Sys_log> {

    /**
     * 获取分表dao
     *
     * @return
     */
    Dao logDao();

    /**
     * 保存日志
     *
     * @param syslog 日志对象
     */
    void save(Sys_log syslog);

    /**
     * 分页查询日志
     *
     * @param status        操作状态
     * @param type          日志类型
     * @param appId         应用ID
     * @param tag           日志标签
     * @param msg           日志内容
     * @param userId        用户ID
     * @param loginname     用户名
     * @param username      用户姓名
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param pageOrderName 排序字段
     * @param pageOrderBy   排序方式
     * @param pageNumber    页码
     * @param pageSize      页大小
     * @return
     */
    Pagination list(String status, LogType type, String appId, String tag, String msg, String userId, String loginname, String username, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize);
}
