package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/app")
@SLog(tag = "应用管理")
@ApiDefinition(tag = "应用管理")
@Slf4j
public class SysAppController {
    @Inject
    private SysAppService sysAppService;
    @Inject
    private SysUserService sysUserService;

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.app")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.success().addData(sysAppService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "创建应用")
    @ApiFormParams(
            implementation = Sys_app.class
    )
    @ApiResponses
    @SLog("创建应用,应用名:${app.name}")
    @SaCheckPermission("sys.manage.app.create")
    public Result<?> create(@Param("..") Sys_app app, HttpServletRequest req) {
        app.setCreatedBy(SecurityUtil.getUserId());
        sysAppService.insert(app);
        sysAppService.cacheClear();
        sysUserService.cacheClear();
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改应用")
    @ApiFormParams(
            implementation = Sys_app.class
    )
    @ApiResponses
    @SLog("修改应用,应用名:${app.name}")
    @SaCheckPermission("sys.manage.app.update")
    public Result<?> update(@Param("..") Sys_app app, HttpServletRequest req) {
        if (app.isDisabled() && GlobalConstant.DEFAULT_COMMON_APPID.equalsIgnoreCase(app.getId())) {
            return Result.error("COMMON 应用不可禁用");
        }
        if (app.isDisabled() && GlobalConstant.DEFAULT_PLATFORM_APPID.equalsIgnoreCase(app.getId())) {
            return Result.error("PLATFORM 应用不可禁用");
        }
        app.setUpdatedBy(SecurityUtil.getUserId());
        sysAppService.updateIgnoreNull(app);
        sysAppService.cacheClear();
        sysUserService.cacheClear();
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取应用")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.app")
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_app app = sysAppService.fetch(id);
        if (app == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(app);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除应用")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除应用,应用名:")
    @SaCheckPermission("sys.manage.app.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_app app = sysAppService.fetch(id);
        if (app == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        if (GlobalConstant.DEFAULT_COMMON_APPID.equalsIgnoreCase(app.getId())) {
            return Result.error("COMMON 应用不可删除");
        }
        if (GlobalConstant.DEFAULT_PLATFORM_APPID.equalsIgnoreCase(app.getId())) {
            return Result.error("PLATFORM 应用不可删除");
        }
        sysAppService.delete(id);
        sysAppService.cacheClear();
        sysUserService.cacheClear();
        req.setAttribute("_slog_msg", app.getName());
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.app.update")
    @SLog(value = "启用禁用:${id}-")
    @ApiOperation(name = "启用禁用")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键ID", required = true),
                    @ApiFormParam(name = "disabled", description = "disabled=true禁用", required = true)
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_COMMON_APPID.equalsIgnoreCase(id)) {
            return Result.error("COMMON 应用不可禁用");
        }
        if (GlobalConstant.DEFAULT_PLATFORM_APPID.equalsIgnoreCase(id)) {
            return Result.error("PLATFORM 应用不可禁用");
        }
        int res = sysAppService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        sysAppService.cacheClear();
        sysUserService.cacheClear();
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", "禁用");
            } else {
                req.setAttribute("_slog_msg", "启用");
            }
            return Result.success();
        }
        return Result.error();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改应用排序")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "location", description = "排序序号"),
                    @ApiFormParam(name = "id", description = "主键ID")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.app.update")
    public Result<?> location(@Param("location") int location, @Param("id") String id, HttpServletRequest req) {
        sysAppService.update(Chain.make("location", location), Cnd.where("id", "=", id));
        sysAppService.cacheClear();
        sysUserService.cacheClear();
        return Result.success();
    }
}
