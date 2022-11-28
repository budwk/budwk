package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.commons.task.TaskServer;
import com.budwk.app.sys.models.Sys_task;
import com.budwk.app.sys.services.SysTaskHistoryService;
import com.budwk.app.sys.services.SysTaskService;
import com.budwk.starter.common.enums.Validation;
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
@At("/platform/sys/task")
@SLog(tag = "定时任务")
@ApiDefinition(tag = "定时任务")
@Slf4j
public class SysTaskController {
    @Inject
    private SysTaskService sysTaskService;
    @Inject
    private SysTaskHistoryService sysTaskHistoryService;
    @Inject
    private TaskServer taskServer;

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "name", example = "", description = "任务名称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.task")
    public Result<?> list(@Param("name") String name, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.success().addData(sysTaskService.listPage(pageNo, pageSize, cnd));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "创建任务")
    @ApiFormParams(
            implementation = Sys_task.class
    )
    @ApiResponses
    @SLog("创建任务,任务名:${task.name}")
    @SaCheckPermission("sys.manage.task.create")
    public Result<?> create(@Param("..") Sys_task task, HttpServletRequest req) {
        task.setCreatedBy(SecurityUtil.getUserId());
        Sys_task sysTask = sysTaskService.insert(task);
        try {
            taskServer.add(sysTask.getId(), sysTask.getIocName(), sysTask.getJobName(), sysTask.getCron(),
                    sysTask.getNote(), sysTask.getParams());
        } catch (Exception ex) {
            sysTaskService.delete(sysTask.getId());
            return Result.error().addMsg(ex.getMessage());
        }
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除任务")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除任务,任务名:")
    @SaCheckPermission("sys.manage.task.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_task sysTask = sysTaskService.fetch(id);
        try {
            if (taskServer.isExist(sysTask.getId())) {
                taskServer.delete(sysTask.getId());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        sysTaskService.delete(id);
        req.setAttribute("_slog_msg", sysTask.getName());
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @ApiOperation(name = "启用禁用")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键", required = true, check = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, check = true, example = "true", type = "boolean")
            }
    )
    @ApiResponses
    @SLog("启用禁用任务:")
    @SaCheckPermission("sys.manage.task.update")
    public Result<?> changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        Sys_task sysTask = sysTaskService.fetch(id);
        if (disabled) {
            try {
                if (taskServer.isExist(sysTask.getId())) {
                    taskServer.delete(sysTask.getId());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.error().addMsg(e.getMessage());
            }
            req.setAttribute("_slog_msg", "禁用-" + sysTask.getName());
        } else {
            try {
                if (!taskServer.isExist(sysTask.getId())) {
                    taskServer.add(sysTask.getId(), sysTask.getIocName(), sysTask.getJobName(), sysTask.getCron(),
                            sysTask.getNote(), sysTask.getParams());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.error().addMsg(e.getMessage());
            }
            req.setAttribute("_slog_msg", "启用-" + sysTask.getName());
        }
        sysTaskService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "启用禁用")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.task")
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_task task = sysTaskService.fetch(id);
        if (task == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(task);
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改任务")
    @ApiFormParams(
            implementation = Sys_task.class
    )
    @ApiResponses
    @SLog("修改任务,任务名:${task.name}")
    @SaCheckPermission("sys.manage.task.update")
    public Result<?> update(@Param("..") Sys_task task, HttpServletRequest req) {
        task.setUpdatedBy(SecurityUtil.getUserId());
        sysTaskService.updateIgnoreNull(task);
        try {
            if (taskServer.isExist(task.getId())) {
                taskServer.delete(task.getId());
            }
            if (!task.isDisabled()) {
                taskServer.add(task.getId(), task.getIocName(), task.getJobName(), task.getCron(),
                        task.getNote(), task.getParams());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "执行记录")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "taskId", example = "", description = "任务ID", type = "string"),
                    @ApiFormParam(name = "month", example = "202211", description = "年月", required = true, check = true, validation = Validation.MONTH),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.task")
    public Result<?> history(@Param("taskId") String taskId, @Param("month") String month, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        cnd.and("taskId", "=", taskId);
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.success().addData(sysTaskHistoryService.getList(month, pageNo, pageSize, cnd));
    }

    @At("/donow/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "手动执行任务")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("手动执行任务,任务名:")
    @SaCheckPermission("sys.manage.task.update")
    public Result<?> donow(String id, HttpServletRequest req) {
        Sys_task sysTask = sysTaskService.fetch(id);
        if (sysTask == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        taskServer.doNow(sysTask.getId(), sysTask.getIocName(), sysTask.getJobName(), sysTask.getParams());
        req.setAttribute("_slog_msg", sysTask.getName());
        return Result.success();
    }
}
