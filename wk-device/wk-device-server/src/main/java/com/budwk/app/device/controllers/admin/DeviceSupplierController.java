package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_supplier;
import com.budwk.app.device.services.DeviceSupplierService;
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
@At("/admin/supplier")
@SLog(tag = "Device_supplier")
@ApiDefinition(tag = "Device_supplier")
@Slf4j
public class DeviceSupplierController {
    @Inject
    private DeviceSupplierService deviceSupplierService;

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
    @SaCheckPermission("device.supplier")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceSupplierService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_supplier")
    @ApiFormParams(
            implementation = Device_supplier.class
    )
    @ApiResponses
    @SLog("新增Device_supplier:${deviceSupplier.id}")
    @SaCheckPermission("device.supplier.create")
    public Result<?> create(@Param("..") Device_supplier deviceSupplier, HttpServletRequest req) {
        deviceSupplier.setCreatedBy(SecurityUtil.getUserId());
        deviceSupplierService.insert(deviceSupplier);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_supplier")
    @ApiFormParams(
            implementation = Device_supplier.class
    )
    @ApiResponses
    @SLog("修改Device_supplier:${deviceSupplier.name}")
    @SaCheckPermission("device.supplier.update")
    public Result<?> update(@Param("..") Device_supplier deviceSupplier, HttpServletRequest req) {
        deviceSupplier.setUpdatedBy(SecurityUtil.getUserId());
        deviceSupplierService.updateIgnoreNull(deviceSupplier);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_supplier")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.supplier")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_supplier deviceSupplier = deviceSupplierService.fetch(id);
        if (deviceSupplier == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceSupplier);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_supplier")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_supplier:")
    @SaCheckPermission("device.supplier.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_supplier deviceSupplier = deviceSupplierService.fetch(id);
        if (deviceSupplier == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceSupplierService.delete(id);
        req.setAttribute("_slog_msg", deviceSupplier.getId());
        return Result.success();
    }
}