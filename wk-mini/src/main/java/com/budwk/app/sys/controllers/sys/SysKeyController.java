package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_key;
import com.budwk.app.sys.services.SysKeyService;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
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
@At("/platform/sys/key")
@SLog(tag = "密钥管理")
@ApiDefinition(tag = "密钥管理")
@Slf4j
public class SysKeyController {
    @Inject
    private SysKeyService sysKeyService;

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
    @SaCheckPermission("sys.config.key")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.success().addData(sysKeyService.listPage(pageNo, pageSize, cnd));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "创建密钥")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "name", description = "密钥名称", required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("创建密钥,密钥名:${key.name}")
    @SaCheckPermission("sys.config.key.create")
    public Result<?> create(@Param("..") Sys_key key, HttpServletRequest req) throws BaseException {
        sysKeyService.createAppkey(key.getName(), SecurityUtil.getUserId());
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @ApiOperation(name = "启用禁用")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "appid", description = "appid")
            }
    )
    @ApiResponses
    @SLog("启用禁用:appid=${appid}")
    @SaCheckPermission("sys.config.key.update")
    public Result<?> changeDisabled(@Param("appid") String appid, @Param("disabled") boolean disabled, HttpServletRequest req) {
        sysKeyService.updateAppkey(appid, disabled, SecurityUtil.getUserId());
        if (disabled) {
            req.setAttribute("_slog_msg", " 禁用");
        } else {
            req.setAttribute("_slog_msg", " 启用");
        }
        return Result.success();
    }


    @At("/delete/{appid}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除密钥")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "appid", description = "appid", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除密钥:appid=${appid}")
    @SaCheckPermission("sys.config.key.delete")
    public Result<?> delete(String appid, HttpServletRequest req) {
        sysKeyService.deleteAppkey(appid);
        return Result.success();
    }
}
