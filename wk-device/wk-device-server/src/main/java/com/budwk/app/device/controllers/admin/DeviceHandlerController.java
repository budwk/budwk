package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_handler;
import com.budwk.app.device.services.DeviceHandlerService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
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
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author wizzer.cn
 */
@IocBean
@At("/admin/handler")
@SLog(tag = "Device_handler")
@ApiDefinition(tag = "Device_handler")
@Slf4j
public class DeviceHandlerController {
    @Inject
    private DeviceHandlerService deviceHandlerService;

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
    @SaCheckPermission("device.handler")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceHandlerService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_handler")
    @ApiFormParams(
            implementation = Device_handler.class
    )
    @ApiResponses
    @SLog("新增Device_handler:${deviceHandler.id}")
    @SaCheckPermission("device.handler.create")
    public Result<?> create(@Param("..") Device_handler deviceHandler, HttpServletRequest req) {
        deviceHandler.setCreatedBy(SecurityUtil.getUserId());
        deviceHandlerService.insert(deviceHandler);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_handler")
    @ApiFormParams(
            implementation = Device_handler.class
    )
    @ApiResponses
    @SLog("修改Device_handler:${deviceHandler.name}")
    @SaCheckPermission("device.handler.update")
    public Result<?> update(@Param("..") Device_handler deviceHandler, HttpServletRequest req) {
        deviceHandler.setUpdatedBy(SecurityUtil.getUserId());
        deviceHandlerService.updateIgnoreNull(deviceHandler);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_handler")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.handler")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_handler deviceHandler = deviceHandlerService.fetch(id);
        if (deviceHandler == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceHandler);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_handler")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_handler:")
    @SaCheckPermission("device.handler.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_handler deviceHandler = deviceHandlerService.fetch(id);
        if (deviceHandler == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceHandlerService.delete(id);
        req.setAttribute("_slog_msg", deviceHandler.getId());
        return Result.success();
    }
}