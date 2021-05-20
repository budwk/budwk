package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.provider.ISysLogProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/sys/log")
@SLog(tag = "日志管理")
@ApiDefinition(tag = "日志管理")
@Slf4j
public class SysLogController {
    @Inject
    @Reference(check = false)
    private ISysLogProvider sysLogProvider;
    @Inject
    private SysAppService sysAppService;

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "type", example = "LOGIN", description = "日志类型"),
                    @ApiFormParam(name = "appId", example = "PLATFORM", description = "应用ID"),
                    @ApiFormParam(name = "tag", example = "", description = "日志标签"),
                    @ApiFormParam(name = "msg", example = "", description = "消息内容"),
                    @ApiFormParam(name = "loginname", example = "superadmin", description = "用户名"),
                    @ApiFormParam(name = "username", example = "", description = "姓名或昵称"),
                    @ApiFormParam(name = "searchDate", example = "", description = "时间范围"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.log")
    public Result<?> list(@Param("type") String type, @Param("appId") String appId,
                          @Param("tag") String tag,
                          @Param("msg") String msg,
                          @Param("loginname") String loginname,
                          @Param("username") String username,
                          @Param("searchDate") String searchDate,
                          @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        LogType logType = null;
        if (Strings.isNotBlank(type)) {
            logType = LogType.from(type);
        }
        String[] date = Strings.splitIgnoreBlank(searchDate, ",");
        long startTime = 0, endTime = 0;
        if (date != null && date.length > 0) {
            startTime = Times.D(date[0]).getTime();
        }
        if (date != null && date.length > 1) {
            endTime = Times.D(date[1]).getTime();
        }
        return Result.success().addData(sysLogProvider.list(logType, appId, tag, msg, loginname, username, startTime, endTime, pageOrderName, pageOrderBy, pageNo, pageSize));
    }

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取配置数据")
    @SaCheckPermission("sys.manage.log")
    public Result<?> data() {
        NutMap map = NutMap.NEW();
        map.addv("apps", sysAppService.listAll());
        map.addv("types", LogType.values());
        return Result.data(map);
    }
}
