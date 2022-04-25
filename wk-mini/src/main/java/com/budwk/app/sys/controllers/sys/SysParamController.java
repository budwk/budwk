package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.enums.SysConfigType;
import com.budwk.app.sys.models.Sys_config;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.app.sys.services.SysConfigService;
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
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/param")
@SLog(tag = "系统参数")
@ApiDefinition(tag = "系统参数")
@Slf4j
public class SysParamController {

    @Inject
    private SysConfigService sysConfigService;
    @Inject
    private SysAppService sysAppService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取配置数据")
    @SaCheckPermission("sys.config.param")
    public Result<?> data() {
        NutMap map = NutMap.NEW();
        map.addv("apps", sysAppService.listAll());
        map.addv("types", SysConfigType.values());
        return Result.data(map);
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "appId", example = "PLATFORM", description = "应用ID"),
                    @ApiFormParam(name = "configKey", example = "", description = "参数Key"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.config.param")
    public Result<?> list(@Param("appId") String appId, @Param("configKey") String configKey, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(appId)) {
            cnd.and("appId", "=", appId);
        }
        if (Strings.isNotBlank(configKey)) {
            cnd.and("configKey", "like", "%" + configKey + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(sysConfigService.listPage(pageNo, pageSize, cnd));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增参数")
    @ApiFormParams(
            implementation = Sys_config.class
    )
    @ApiResponses
    @SLog("新增参数,参数:${config.configKey}")
    @SaCheckPermission("sys.config.param.create")
    public Result<?> create(@Param("..") Sys_config config, HttpServletRequest req) {
        config.setCreatedBy(SecurityUtil.getUserId());
        if (sysConfigService.count(Cnd.where("configKey", "=", config.getConfigKey()).and("appId", "=", GlobalConstant.DEFAULT_COMMON_APPID)) > 0) {
            return Result.error("新增参数 {} 与公共参数重复", config.getConfigKey());
        }
        sysConfigService.insert(config);
        sysConfigService.cacheClear();
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改参数")
    @ApiFormParams(
            implementation = Sys_config.class
    )
    @ApiResponses
    @SLog("修改参数,参数:${config.configKey}")
    @SaCheckPermission("sys.config.param.update")
    public Result<?> update(@Param("..") Sys_config config, HttpServletRequest req) {
        config.setUpdatedBy(SecurityUtil.getUserId());
        if (sysConfigService.count(Cnd.where("configKey", "=", config.getConfigKey()).and("appId", "=", GlobalConstant.DEFAULT_COMMON_APPID).and("id", "<>", config.getId())) > 0) {
            return Result.error("参数 {} 与公共参数重复", config.getConfigKey());
        }
        sysConfigService.updateIgnoreNull(config);
        sysConfigService.cacheClear();
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取参数")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.config.param")
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_config config = sysConfigService.fetch(id);
        if (config == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(config);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除参数")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除参数,参数:")
    @SaCheckPermission("sys.config.param.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_config config = sysConfigService.fetch(id);
        if (config == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        if (GlobalConstant.DEFAULT_COMMON_APPID.equalsIgnoreCase(config.getAppId()) && config.getConfigKey().startsWith("App")) {
            return Result.error("系统内置公共参数不可删除");
        }
        sysConfigService.delete(id);
        sysConfigService.cacheClear();
        req.setAttribute("_slog_msg", config.getConfigKey());
        return Result.success();
    }
}
