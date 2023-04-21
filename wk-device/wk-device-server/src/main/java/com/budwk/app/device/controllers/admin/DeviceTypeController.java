package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_type;
import com.budwk.app.device.services.DeviceTypeService;
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
@At("/admin/type")
@SLog(tag = "Device_type")
@ApiDefinition(tag = "Device_type")
@Slf4j
public class DeviceTypeController {
    @Inject
    private DeviceTypeService deviceTypeService;

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
    @SaCheckPermission("device.type")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceTypeService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_type")
    @ApiFormParams(
            implementation = Device_type.class
    )
    @ApiResponses
    @SLog("新增Device_type:${deviceType.id}")
    @SaCheckPermission("device.type.create")
    public Result<?> create(@Param("..") Device_type deviceType, HttpServletRequest req) {
        deviceType.setCreatedBy(SecurityUtil.getUserId());
        deviceTypeService.insert(deviceType);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_type")
    @ApiFormParams(
            implementation = Device_type.class
    )
    @ApiResponses
    @SLog("修改Device_type:${deviceType.name}")
    @SaCheckPermission("device.type.update")
    public Result<?> update(@Param("..") Device_type deviceType, HttpServletRequest req) {
        deviceType.setUpdatedBy(SecurityUtil.getUserId());
        deviceTypeService.updateIgnoreNull(deviceType);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_type")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.type")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_type deviceType = deviceTypeService.fetch(id);
        if (deviceType == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceType);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_type")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_type:")
    @SaCheckPermission("device.type.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_type deviceType = deviceTypeService.fetch(id);
        if (deviceType == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceTypeService.delete(id);
        req.setAttribute("_slog_msg", deviceType.getId());
        return Result.success();
    }
}