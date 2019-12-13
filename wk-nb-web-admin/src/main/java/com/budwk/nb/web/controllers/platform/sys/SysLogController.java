package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.slog.services.SLogSerivce;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

/**
 * Created by wizzer.cn on 2019/12/11
 */
@IocBean
@At("/api/{version}/platform/sys/log")
@Ok("json")
@ApiVersion("1.0.0")
public class SysLogController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SLogSerivce sLogSerivce;

    /**
     * @api {post} /api/1.0.0/platform/sys/log/list 分页查询
     * @apiName list
     * @apiGroup SYS_LOG
     * @apiPermission sys.manage.log
     * @apiVersion 1.0.0
     * @apiParam {String} searchDate   日期范围
     * @apiParam {String} searchType   日志类型
     * @apiParam {String} pageNo       页码
     * @apiParam {String} pageSize     页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.log")
    public Object list(@Param("searchDate") String searchDate, @Param("searchType") String searchType, @Param("loginname") String loginname, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            String[] date = StringUtils.split(searchDate, ",");
            long startTime = 0, endTime = 0;
            if (date != null && date.length > 0) {
                startTime = Times.D(date[0]).getTime();
            }
            if (date != null && date.length > 1) {
                endTime = Times.D(date[1]).getTime();
            }
            return Result.success().addData(sLogSerivce.list(searchType, loginname, startTime, endTime, pageOrderName, PageUtil.getOrder(pageOrderBy), pageNo, pageSize));
        } catch (Exception e) {
            return Result.error();
        }
    }
}
