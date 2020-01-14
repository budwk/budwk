package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.models.Sys_app_conf;
import com.budwk.nb.sys.models.Sys_app_list;
import com.budwk.nb.sys.models.Sys_app_task;
import com.budwk.nb.sys.services.SysAppConfService;
import com.budwk.nb.sys.services.SysAppListService;
import com.budwk.nb.sys.services.SysAppTaskService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelCommand;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelProperty;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelService;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/10
 */
@IocBean
@At("/api/{version}/platform/sys/app")
@Ok("json")
@ApiVersion("1.0.0")
public class SysAppController {
    private static final Log log = Logs.get();
    @Inject
    private LoglevelService loglevelService;
    @Inject
    @Reference
    private SysAppListService sysAppListService;
    @Inject
    @Reference
    private SysAppConfService sysAppConfService;
    @Inject
    @Reference
    private SysAppTaskService sysAppTaskService;
    @Inject
    private RedisService redisService;

    /**
     * @api {get} /api/1.0.0/platform/sys/app/host_data 获取主机数据
     * @apiName host_data
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} hostName   主机名
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  主机数据
     */
    @At("/host_data")
    @Ok("json:full")
    @RequiresPermissions("sys.server.app")
    @SuppressWarnings("unchecked")
    public Object getHostData(@Param("hostName") String hostName) {
        try {
            List<NutMap> hostList = new ArrayList<>();
            NutMap map = loglevelService.getData();
            List<LoglevelProperty> dataList = new ArrayList<>();
            //对数据进行整理,获得左侧主机列表及右侧实例数据
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                List<LoglevelProperty> list = (List) entry.getValue();
                for (LoglevelProperty property : list) {
                    NutMap nutMap = NutMap.NEW().addv("hostName", property.getHostName()).addv("hostAddress", property.getHostAddress());
                    if (!hostList.contains(nutMap))
                        hostList.add(nutMap);
                    if (Strings.isBlank(hostName) || (Strings.isNotBlank(hostName) && property.getHostName().equals(hostName))) {
                        dataList.add(property);
                    }
                }
            }
            return Result.success().addData(NutMap.NEW().addv("hostList", hostList).addv("appList", dataList));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/os_data 获取资源占用数据
     * @apiName os_data
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} hostName   主机名
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/os_data")
    @Ok("json:full")
    @RequiresPermissions("sys.server.app")
    @SuppressWarnings("unchecked")
    public Object getOsData(@Param("hostName") String hostName) {
        try {
            Set<String> set = redisService.keys("logback:deploy:" + hostName + ":*");
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            List<NutMap> dataList = new ArrayList<>();
            for (String key : list) {
                dataList.add(Json.fromJson(NutMap.class, redisService.get(key)));
            }
            return Result.success().addData(dataList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/version 获取最新版本信息
     * @apiName version
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} name   实例名
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/version")
    @Ok("json")
    @RequiresPermissions("sys.server.app")
    public Object version(@Param("name") String name) {
        try {
            List<Sys_app_list> appVerList = sysAppListService.query(Cnd.where("disabled", "=", false).and("appName", "=", name).desc("createdAt"), new Pager().setPageNumber(1).setPageSize(10));
            List<Sys_app_conf> confVerList = sysAppConfService.query(Cnd.where("disabled", "=", false).and("confName", "=", name).desc("createdAt"), new Pager().setPageNumber(1).setPageSize(10));
            return Result.success().addData(NutMap.NEW().addv("appVerList", appVerList).addv("confVerList", confVerList));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/version 创建执行任务
     * @apiName version
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} appTask   任务表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/task/create")
    @Ok("json")
    @RequiresPermissions("sys.server.app.instance")
    @SLog(tag = "创建任务", msg = "应用名称:${appTask.getName()} 动作:${appTask.getAction()}")
    public Object taskAddDo(@Param("..") Sys_app_task appTask, HttpServletRequest req) {
        try {
            Cnd cnd = Cnd.where("name", "=", appTask.getName()).and("action", "=", "stop")
                    .and("appVersion", "=", appTask.getAppVersion())
                    .and("confVersion", "=", appTask.getConfVersion())
                    .and("hostName", "=", appTask.getHostName())
                    .and("hostAddress", "=", appTask.getHostAddress())
                    .and(Cnd.exps("status", "=", 0).or("status", "=", 1));
            if ("stop".equals(appTask.getAction())) {
                cnd.and("processId", "=", appTask.getProcessId());
            }
            int num = sysAppTaskService.count(cnd);
            if (num > 0) {
                return Result.error("sys.server.app.txt.task.exist");
            }
            appTask.setCreatedBy(StringUtil.getPlatformUid());
            appTask.setUpdatedBy(StringUtil.getPlatformUid());
            appTask.setStatus(0);
            sysAppTaskService.insert(appTask);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/task/list 获取任务列表
     * @apiName task/list
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/task/list")
    @Ok("json:{locked:'confData',ignoreNull:false}")
    @RequiresPermissions("sys.server.app")
    public Object getTaskList(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysAppTaskService.listPageLinks(pageNo, pageSize, cnd, "^(user)$"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/task/cannel 取消任务执行
     * @apiName task/cannel
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} id   任务ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/task/cannel/?")
    @Ok("json")
    @RequiresPermissions("sys.server.app.instance")
    @SLog(tag = "取消任务", msg = "任务ID:${id}")
    public Object taskCannel(String id, HttpServletRequest req) {
        try {
            //加上status条件,防止执行前状态已变更
            sysAppTaskService.update(Chain.make("status", 4), Cnd.where("id", "=", id).and("status", "=", 0));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/log/level 更改任务等级
     * @apiName log/level
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} name   实例名
     * @apiParam {String} action   动作
     * @apiParam {String} processId   进程ID
     * @apiParam {String} loglevel   日志等级
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/log/level")
    @Ok("json")
    @RequiresPermissions("sys.server.app.loglevel")
    public Object changeLogLevel(@Param("action") String action, @Param("name") String name, @Param("processId") String processId, @Param("loglevel") String loglevel) {
        try {
            LoglevelCommand loglevelCommand = new LoglevelCommand();
            loglevelCommand.setAction(action);
            loglevelCommand.setLevel(loglevel);
            if ("processId".equals(action)) {
                loglevelCommand.setProcessId(processId);
            }
            loglevelCommand.setName(name);
            loglevelService.changeLoglevel(loglevelCommand);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
