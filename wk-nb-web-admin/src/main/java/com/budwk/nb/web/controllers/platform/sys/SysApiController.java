package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.services.SysApiService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/9
 */
@IocBean
@At("/api/{version}/platform/sys/api")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_接口管理")}, servers = @Server(url = "/"))
public class SysApiController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysApiService sysApiService;


    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.config.api")
    @Operation(
            tags = "系统_接口管理", summary = "分页查询接口列表",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.api")
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = sysApiService.listPage(pageNo, pageSize, cnd);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.api.create")
    @SLog(tag = "新建密钥", msg = "应用名称:${name}")
    @Operation(
            tags = "系统_接口管理", summary = "新建密钥",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.api.create")
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
                    @ApiFormParam(name = "name", example = "", description = "接口名称")
            }
    )
    public Object create(@Param("name") String name, HttpServletRequest req) {
        try {
            sysApiService.createAppkey(name, StringUtil.getPlatformUid());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/delete/{appid}")
    @DELETE
    @Ok("json")
    @RequiresPermissions("sys.config.api.delete")
    @SLog(tag = "删除密钥", msg = "Appid:${appid}")
    @Operation(
            tags = "系统_接口管理", summary = "删除密钥",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.api.delete")
            },
            parameters = {
                    @Parameter(name = "appid", description = "appid", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String appid, HttpServletRequest req) {
        try {
            sysApiService.deleteAppkey(appid);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.api.update")
    @SLog(tag = "启用禁用密钥")
    @Operation(
            tags = "系统_接口管理", summary = "启用禁用密钥",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.api.update")
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
                    @ApiFormParam(name = "appid", description = "appid", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("appid") String appid, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysApiService.updateAppkey(appid, disabled, StringUtil.getPlatformUid());
            if (disabled) {
                req.setAttribute("_slog_msg", "appid:" + appid + " " + Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", "appid:" + appid + " " + Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            sysApiService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
