package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_task;
import com.budwk.nb.sys.services.SysTaskService;
import com.budwk.nb.task.services.TaskPlatformService;
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
import org.nutz.dao.Chain;
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
 * @author wizzer(wizzer @ qq.com) on 2019/12/14
 */
@IocBean
@At("/api/{version}/platform/sys/task")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_定时任务")}, servers = @Server(url = "/"))
public class SysTaskController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysTaskService sysTaskService;
    @Inject
    @Reference(check = false)
    private TaskPlatformService taskPlatformService;


    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.task")
    @Operation(
            tags = "系统_定时任务", summary = "分页查询定时任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysTaskService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.create")
    @SLog(tag = "新增任务", msg = "任务名称:${task.name}")
    @Operation(
            tags = "系统_定时任务", summary = "新增定时任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task.create")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_task.class
    )
    public Object create(@Param("..") Sys_task task, HttpServletRequest req) {
        try {
            task.setCreatedBy(StringUtil.getPlatformUid());
            Sys_task sysTask = sysTaskService.insert(task);
            try {
                taskPlatformService.add(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                        sysTask.getNote(), sysTask.getData());
            } catch (Exception ex) {
                sysTaskService.delete(sysTask.getId());
                return Result.error().addMsg(ex.getMessage());
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.task.delete")
    @SLog(tag = "删除任务")
    @Operation(
            tags = "系统_定时任务", summary = "删除定时任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "定时任务ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String id, HttpServletRequest req) {
        try {
            Sys_task sysTask = sysTaskService.fetch(id);
            try {
                if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                    taskPlatformService.delete(sysTask.getId(), sysTask.getId());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            sysTaskService.delete(id);
            req.setAttribute("_slog_msg", sysTask.getName());
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.update")
    @SLog(tag = "启用禁用任务")
    @Operation(
            tags = "系统_定时任务", summary = "启用禁用定时任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task.update")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "id", description = "主键", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            Sys_task sysTask = sysTaskService.fetch(id);
            if (disabled) {
                try {
                    if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                        taskPlatformService.delete(sysTask.getId(), sysTask.getId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return Result.error().addMsg(e.getMessage());
                }
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off") + " " + sysTask.getName());
            } else {
                try {
                    if (!taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                        taskPlatformService.add(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                                sysTask.getNote(), sysTask.getData());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return Result.error().addMsg(e.getMessage());
                }
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on") + " " + sysTask.getName());
            }
            sysTaskService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }


    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.task")
    @Operation(
            tags = "系统_用户管理", summary = "获取用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task")
            },
            parameters = {
                    @Parameter(name = "id", description = "定时任务ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_task task = sysTaskService.fetch(id);
            if (task == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(task);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.update")
    @SLog(tag = "修改任务", msg = "任务名称:${task.name}")
    @Operation(
            tags = "系统_定时任务", summary = "修改定时任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.task.update")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_task.class
    )
    public Object update(@Param("..") Sys_task task, HttpServletRequest req) {
        try {
            task.setUpdatedBy(StringUtil.getPlatformUid());
            sysTaskService.updateIgnoreNull(task);
            try {
                if (taskPlatformService.isExist(task.getId(), task.getId())) {
                    taskPlatformService.delete(task.getId(), task.getId());
                }
                if (!task.isDisabled()) {
                    taskPlatformService.add(task.getId(), task.getId(), task.getJobClass(), task.getCron(),
                            task.getNote(), task.getData());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

}
