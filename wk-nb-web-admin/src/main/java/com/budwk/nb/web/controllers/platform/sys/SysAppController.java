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
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

    /**
     * @api {get} /api/1.0.0/platform/sys/app/jar/list 获取Jar包列表
     * @apiName jar/list
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.jar
     * @apiVersion 1.0.0
     * @apiParam {String} appName   实例名
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/jar/list")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.server.app.jar")
    public Object jarData(@Param("appName") String appName, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(appName)) {
                cnd.and("appName", "like", "%" + appName + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysAppListService.listPageLinks(pageNo, pageSize, cnd, "^(user)$"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/jar/create 添加Jar包
     * @apiName jar/create
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.jar.create
     * @apiVersion 1.0.0
     * @apiParam {Object} sysAppList   表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/jar/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.jar.create")
    @SLog(tag = "添加安装包", msg = "应用名称:${sysAppList.appName}")
    public Object jarCreate(@Param("..") Sys_app_list sysAppList, HttpServletRequest req) {
        try {
            int num = sysAppListService.count(Cnd.where("appName", "=", Strings.trim(sysAppList.getAppName())).and("appVersion", "=", Strings.trim(sysAppList.getAppVersion())));
            if (num > 0) {
                return Result.error("sys.server.app.txt.version.exist");
            }
            sysAppList.setAppName(Strings.trim(sysAppList.getAppName()));
            sysAppList.setAppVersion(Strings.trim(sysAppList.getAppVersion()));
            sysAppList.setCreatedBy(StringUtil.getPlatformUid());
            sysAppList.setUpdatedBy(StringUtil.getPlatformUid());
            sysAppListService.insert(sysAppList);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/jar/search 查询Jar包
     * @apiName jar/search
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.jar
     * @apiVersion 1.0.0
     * @apiParam {String} appName   实例名
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/jar/search")
    @Ok("json")
    @RequiresPermissions("sys.server.app.jar")
    public Object jarSearch(@Param("appName") String appName) {
        return Result.NEW().addData(sysAppListService.getAppNameList());
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/jar/delete/:id 删除Jar包
     * @apiName jar/delete
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.jar.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/jar/delete/?")
    @Ok("json")
    @RequiresPermissions("sys.server.app.jar.delete")
    @SLog(tag = "删除Jar包")
    public Object jarDelete(String id, HttpServletRequest req) {
        try {
            Sys_app_list appList = sysAppListService.fetch(id);
            if (appList == null) {
                return Result.error("system.error.noData");
            }
            sysAppListService.delete(id);
            req.setAttribute("_slog_msg", appList.getAppName());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/jar/disabled 启用禁用Jar包
     * @apiName jar/disabled
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.jar.update
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiParam {String} disabled   true/false
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/jar/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.jar.update")
    @SLog(tag = "启用禁用Jar包")
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysAppListService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/app/conf/list 获取配置列表
     * @apiName conf/list
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf
     * @apiVersion 1.0.0
     * @apiParam {String} appName   实例名
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/list")
    @Ok("json:{locked:'confData|password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.server.app.conf")
    public Object confData(@Param("confName") String confName, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(confName)) {
                cnd.and("confName", "like", "%" + confName + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysAppConfService.listPageLinks(pageNo, pageSize, cnd, "^(user)$"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/create 添加Jar包
     * @apiName conf/create
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf.create
     * @apiVersion 1.0.0
     * @apiParam {Object} sysAppConf   表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/create")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.create")
    @SLog(tag = "添加配置文件", msg = "应用名称:${sysAppConf.confName}")
    public Object confAddDo(@Param("..") Sys_app_conf sysAppConf, HttpServletRequest req) {
        try {
            int num = sysAppConfService.count(Cnd.where("confName", "=", Strings.trim(sysAppConf.getConfName())).and("confVersion", "=", Strings.trim(sysAppConf.getConfVersion())));
            if (num > 0) {
                return Result.error("sys.server.app.txt.version.exist");
            }
            sysAppConf.setConfName(Strings.trim(sysAppConf.getConfName()));
            sysAppConf.setConfVersion(Strings.trim(sysAppConf.getConfVersion()));
            sysAppConf.setCreatedBy(StringUtil.getPlatformUid());
            sysAppConf.setUpdatedBy(StringUtil.getPlatformUid());
            sysAppConfService.insert(sysAppConf);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/search 查询配置
     * @apiName conf/search
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf
     * @apiVersion 1.0.0
     * @apiParam {Object} sysAppConf   表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/search")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf")
    public Object confSearch(@Param("confName") String confName) {
        return Result.NEW().addData(sysAppConfService.getConfNameList());
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/delete/:id 删除配置
     * @apiName conf/delete
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/delete/?")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.delete")
    @SLog(tag = "删除配置文件")
    public Object confDelete(String id, HttpServletRequest req) {
        try {
            Sys_app_conf appConf = sysAppConfService.fetch(id);
            if (appConf == null) {
                return Result.error("system.error.noData");
            }
            sysAppConfService.delete(id);
            req.setAttribute("_slog_msg", appConf.getConfName());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/download/:id 下载配置文件
     * @apiName download/delete
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiSuccess {Object} data  文件流
     */
    @At("/conf/download/?")
    @Ok("void")
    @RequiresPermissions("sys.server.app.conf")
    public void confDownload(String id, HttpServletResponse response) {
        try {
            Sys_app_conf conf = sysAppConfService.fetch(id);
            String fileName = conf.getConfName() + "-" + conf.getConfVersion() + ".properties";
            response.setHeader("Content-Type", "text/properties");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            try (InputStream in = new StringInputStream(conf.getConfData())) {
                Streams.writeAndClose(response.getOutputStream(), in);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/get/:id 获取配置内容
     * @apiName conf/get
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/get/?")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf")
    public Object confGet(String id, HttpServletRequest req) {
        try {
            Sys_app_conf appConf = sysAppConfService.fetch(id);
            if (appConf == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(appConf);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/update 修改配置
     * @apiName conf/update
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf.update
     * @apiVersion 1.0.0
     * @apiParam {Object} sysAppConf   表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/conf/update")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.update")
    @SLog(tag = "修改配置文件", msg = "应用名称:${sysAppConf.confName}")
    public Object confEditDo(@Param("..") Sys_app_conf sysAppConf, HttpServletRequest req) {
        try {
            sysAppConf.setUpdatedBy(StringUtil.getPlatformUid());
            sysAppConfService.updateIgnoreNull(sysAppConf);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/app/conf/disabled 启用禁用Jar包
     * @apiName conf/disabled
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app.conf.update
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiParam {String} disabled   true/false
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/conf/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.conf.update")
    @SLog(tag = "启用禁用Jar包")
    public Object changeConfDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysAppConfService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
