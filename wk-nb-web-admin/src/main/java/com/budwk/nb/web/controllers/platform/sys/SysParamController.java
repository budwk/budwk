package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_config;
import com.budwk.nb.sys.services.SysConfigService;
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
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/12/10
 */
@IocBean
@At("/api/{version}/platform/sys/param")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_系统参数")}, servers = @Server(url = "/"))
public class SysParamController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysConfigService sysConfigService;
    @Inject
    private PubSubService pubSubService;

    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.param")
    @Operation(
            tags = "系统_系统参数", summary = "分页查询系统参数",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.param")
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
            Pagination pagination = sysConfigService.listPageLinks(pageNo, pageSize, cnd, "^(createdByUser|updatedByUser)$");
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.param.create")
    @SLog(tag = "新增参数")
    @Operation(
            tags = "系统_系统参数", summary = "新增参数",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.param.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_config.class
    )
    public Object create(@Param("..") Sys_config config, HttpServletRequest req) {
        try {
            if (sysConfigService.insert(config) != null) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("%s=>%s", config.getConfigKey(), config.getConfigValue()));
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.param")
    @Operation(
            tags = "系统_系统参数", summary = "获取参数信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.param")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getData(String id) {
        try {
            Sys_config config = sysConfigService.fetch(id);
            if (config == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(config);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.param.update")
    @SLog(tag = "修改参数")
    @Operation(
            tags = "系统_系统参数", summary = "修改参数",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.param.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_config.class
    )
    public Object update(@Param("..") Sys_config config, HttpServletRequest req) {
        try {
            config.setUpdatedBy(StringUtil.getPlatformUid());
            if (sysConfigService.updateIgnoreNull(config) > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("%s=>%s", config.getConfigKey(), config.getConfigValue()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{configKey}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.param.delete")
    @SLog(tag = "删除参数")
    @Operation(
            tags = "系统_系统参数", summary = "删除参数",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.param.delete")
            },
            parameters = {
                    @Parameter(name = "configKey", description = "configKey", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String configKey, HttpServletRequest req) {
        try {
            if (Strings.sBlank(configKey).startsWith("App")) {
                return Result.error("sys.manage.param.delete.not");
            }
            if (sysConfigService.delete(configKey) > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("key==%s", configKey));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
