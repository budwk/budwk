package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_app_conf;
import com.budwk.nb.sys.models.Sys_app_list;
import com.budwk.nb.sys.models.Sys_app_task;
import com.budwk.nb.sys.services.SysAppConfService;
import com.budwk.nb.sys.services.SysAppListService;
import com.budwk.nb.sys.services.SysAppTaskService;
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
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_APP_DEPLOY;
import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_WSROOM;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/10
 */
@IocBean
@At("/api/{version}/platform/sys/app")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_应用管理")}, servers = @Server(url = "/"))
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

    @At("/host_data")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.server.app")
    @Operation(
            tags = "系统_应用管理", summary = "获取主机数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app")
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
                    @ApiFormParam(name = "hostName", description = "主机名")
            }
    )
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


    @At("/os_data")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.server.app")
    @Operation(
            tags = "系统_应用管理", summary = "获取主机统计数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app")
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
                    @ApiFormParam(name = "hostName", description = "主机名")
            }
    )
    @SuppressWarnings("unchecked")
    public Object getOsData(@Param("hostName") String hostName) {
        try {
            List<String> list = new ArrayList<>();
            ScanParams match = new ScanParams().match(REDIS_KEY_APP_DEPLOY + hostName + ":*");
            ScanResult<String> scan = null;
            do {
                scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                list.addAll(scan.getResult());//增量式迭代查询,可能还有下个循环,应该是追加
            } while (!scan.isCompleteIteration());
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

    @At("/version")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app")
    @Operation(
            tags = "系统_应用管理", summary = "获取应用和配置文件版本号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app")
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
                    @ApiFormParam(name = "name", description = "应用实例名")
            }
    )
    public Object version(@Param("name") String name) {
        try {
            List<Sys_app_list> appVerList = sysAppListService.query(Cnd.where("disabled", "=", false).and("appName", "=", name).desc("createdAt"), new Pager().setPageNumber(1).setPageSize(10));
            List<Sys_app_conf> confVerList = sysAppConfService.query(Cnd.where("disabled", "=", false).and("confName", "=", name).desc("createdAt"), new Pager().setPageNumber(1).setPageSize(10));
            return Result.success().addData(NutMap.NEW().addv("appVerList", appVerList).addv("confVerList", confVerList));
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/task/create")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app.instance")
    @SLog(tag = "创建实例执行任务", msg = "应用名称:${appTask.getName()} 动作:${appTask.getAction()}")
    @Operation(
            tags = "系统_应用管理", summary = "创建实例执行任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app")
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
            implementation = Sys_app_task.class
    )
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


    @At("/task/list")
    @POST
    @Ok("json:{locked:'confData|password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.server.app")
    @Operation(
            tags = "系统_应用管理", summary = "分页查询实例任务列表",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app")
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

    @At("/task/cannel/{id}")
    @GET
    @Ok("json")
    @RequiresPermissions("sys.server.app.instance")
    @SLog(tag = "取消任务", msg = "任务ID:${id}")
    @Operation(
            tags = "系统_应用管理", summary = "取消实例执行任务",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.instance")
            },
            parameters = {
                    @Parameter(name = "id", description = "任务ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object taskCannel(String id, HttpServletRequest req) {
        try {
            //加上status条件,防止执行前状态已变更
            sysAppTaskService.update(Chain.make("status", 4), Cnd.where("id", "=", id).and("status", "=", 0));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/log/level")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app.loglevel")
    @Operation(
            tags = "系统_应用管理", summary = "更改实例日志等级",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.loglevel")
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
                    @ApiFormParam(name = "action", description = "执行方式", example = "processId", required = true),
                    @ApiFormParam(name = "name", description = "实例名称", required = true),
                    @ApiFormParam(name = "processId", description = "进程ID", required = true),
                    @ApiFormParam(name = "loglevel", description = "日志等级", required = true)
            }
    )
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


    @At("/jar/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.server.app.jar")
    @Operation(
            tags = "系统_应用管理", summary = "分页查询JAR列表",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.jar")
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
                    @ApiFormParam(name = "appName", example = "", description = "实例名称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
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

    @At("/jar/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.jar.create")
    @SLog(tag = "添加安装包", msg = "应用名称:${sysAppList.appName}")
    @Operation(
            tags = "系统_应用管理", summary = "添加JAR安装包",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.jar.create")
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
            implementation = Sys_app_list.class
    )
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


    @At("/jar/search")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app.jar")
    @Operation(
            tags = "系统_应用管理", summary = "搜索JAR包",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.jar")
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
                    @ApiFormParam(name = "appName", description = "实例名称")
            }
    )
    public Object jarSearch(@Param("appName") String appName) {
        return Result.NEW().addData(sysAppListService.getAppNameList());
    }

    @At("/jar/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.server.app.jar.delete")
    @SLog(tag = "删除Jar包")
    @Operation(
            tags = "系统_应用管理", summary = "删除Jar包",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.jar.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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


    @At("/jar/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.jar.update")
    @SLog(tag = "启用禁用Jar包")
    @Operation(
            tags = "系统_应用管理", summary = "启用禁用Jar包",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.jar.update")
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
            int res = sysAppListService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/conf/list")
    @POST
    @Ok("json:{locked:'confData|password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.server.app.conf")
    @Operation(
            tags = "系统_应用管理", summary = "分页查询配置列表",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf")
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
                    @ApiFormParam(name = "confName", example = "", description = "实例名称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
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

    @At("/conf/create")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.create")
    @SLog(tag = "添加配置文件", msg = "应用名称:${sysAppConf.confName}")
    @Operation(
            tags = "系统_应用管理", summary = "添加配置文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf.create")
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
            implementation = Sys_app_conf.class
    )
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

    @At("/conf/search")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf")
    @Operation(
            tags = "系统_应用管理", summary = "搜索配置文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf")
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
                    @ApiFormParam(name = "confName", example = "", description = "实例名称")
            }
    )
    public Object confSearch(@Param("confName") String confName) {
        return Result.NEW().addData(sysAppConfService.getConfNameList());
    }


    @At("/conf/delete/{id}")
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.delete")
    @SLog(tag = "删除配置文件")
    @Operation(
            tags = "系统_应用管理", summary = "删除配置文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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


    @At("/conf/download/{id}")
    @GET
    @Ok("void")
    @RequiresPermissions("sys.server.app.conf")
    @Operation(
            tags = "系统_应用管理", summary = "下载配置文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "", name = "example"), mediaType = "text/properties"))
            }
    )
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

    @At("/conf/get/{id}")
    @GET
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf")
    @Operation(
            tags = "系统_应用管理", summary = "获取配置文件内容",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/conf/update")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.server.app.conf.update")
    @SLog(tag = "修改配置文件", msg = "应用名称:${sysAppConf.confName}")
    @Operation(
            tags = "系统_应用管理", summary = "修改配置文件内容",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf.update")
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
            implementation = Sys_app_conf.class
    )
    public Object confEditDo(@Param("..") Sys_app_conf sysAppConf, HttpServletRequest req) {
        try {
            sysAppConf.setUpdatedBy(StringUtil.getPlatformUid());
            sysAppConfService.updateIgnoreNull(sysAppConf);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/conf/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.server.app.conf.update")
    @SLog(tag = "启用禁用配置文件")
    @Operation(
            tags = "系统_应用管理", summary = "启用禁用配置文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.server.app.conf.update")
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
    public Object changeConfDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysAppConfService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
