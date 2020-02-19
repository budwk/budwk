package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.slog.services.SLogSerivce;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/11
 */
@IocBean
@At("/api/{version}/platform/sys/log")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_日志管理")}, servers = @Server(url = "/"))
public class SysLogController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SLogSerivce sLogSerivce;

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.log")
    @Operation(
            tags = "系统_日志管理", summary = "分页查询日志",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.log")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "searchDate", example = "\"2020-02-02\",\"2020-03-02\"",type = "string",description = "日期范围数组"),
                    @ApiFormParam(name = "searchType", example = "", description = "日志类型"),
                    @ApiFormParam(name = "loginname", example = "", description = "用户名"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
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
