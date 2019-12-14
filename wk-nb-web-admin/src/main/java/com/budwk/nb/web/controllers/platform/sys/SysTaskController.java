package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.models.Sys_task;
import com.budwk.nb.sys.services.SysTaskService;
import com.budwk.nb.task.services.TaskPlatformService;
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
 * Created by wizzer.cn on 2019/12/14
 */
@IocBean
@At("/api/{version}/platform/sys/task")
@Ok("json")
@ApiVersion("1.0.0")
public class SysTaskController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysTaskService sysTaskService;
    @Inject
    @Reference(check = false)
    private TaskPlatformService taskPlatformService;

    /**
     * @api {post} /api/1.0.0/platform/sys/task/list 分页查询
     * @apiName list
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task
     * @apiVersion 1.0.0
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
    @RequiresPermissions("sys.manage.task")
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

    /**
     * @api {get} /api/1.0.0/platform/sys/task/create 新增任务
     * @apiName create
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task.create
     * @apiVersion 1.0.0
     * @apiParam {Object} task   表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.create")
    @SLog(tag = "新增任务", msg = "任务名称:${task.name}")
    public Object create(@Param("..") Sys_task task, HttpServletRequest req) {
        try {
            task.setCreatedBy(StringUtil.getPlatformUid());
            Sys_task sysTask = sysTaskService.insert(task);
            taskPlatformService.add(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                    sysTask.getNote(), sysTask.getData());
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {delete} /api/1.0.0/platform/sys/task/delete/:id 删除任务
     * @apiName delete
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.task.delete")
    @SLog(tag = "删除任务")
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

    /**
     * @api {post} /api/1.0.0/platform/sys/task/disabled 启用禁用
     * @apiName disabled
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task.update
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiParam {String} disabled   true/false
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.update")
    @SLog(tag = "启用禁用任务")
    public Object changeDisabled(@Param("id") String id, @Param("path") String path, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            Sys_task sysTask = sysTaskService.fetch(id);
            sysTaskService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                try {
                    if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                        taskPlatformService.delete(sysTask.getId(), sysTask.getId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on") + " " + sysTask.getName());
            } else {
                try {
                    if (!taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                        taskPlatformService.add(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                                sysTask.getNote(), sysTask.getData());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off") + " " + sysTask.getName());
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }


    }

    /**
     * @api {get} /api/1.0.0/platform/sys/task/get/:id 获取任务信息
     * @apiName get
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.task")
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

    /**
     * @api {post} /api/1.0.0/platform/sys/task/update 修改任务信息
     * @apiName update
     * @apiGroup SYS_TASK
     * @apiPermission sys.manage.task.update
     * @apiVersion 1.0.0
     * @apiParam {Object} task 表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.task.update")
    @SLog(tag = "修改任务", msg = "任务名称:${task.name}")
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
