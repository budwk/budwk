package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_supplier_code;
import com.budwk.app.device.services.DeviceSupplierCodeService;
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
@At("/admin/suppliercode")
@SLog(tag = "Device_supplier_code")
@ApiDefinition(tag = "Device_supplier_code")
@Slf4j
public class DeviceSupplierCodeController {
    @Inject
    private DeviceSupplierCodeService deviceSupplierCodeService;

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
    @SaCheckPermission("device.suppliercode")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceSupplierCodeService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_supplier_code")
    @ApiFormParams(
            implementation = Device_supplier_code.class
    )
    @ApiResponses
    @SLog("新增Device_supplier_code:${deviceSupplierCode.id}")
    @SaCheckPermission("device.suppliercode.create")
    public Result<?> create(@Param("..") Device_supplier_code deviceSupplierCode, HttpServletRequest req) {
        deviceSupplierCode.setCreatedBy(SecurityUtil.getUserId());
        deviceSupplierCodeService.insert(deviceSupplierCode);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_supplier_code")
    @ApiFormParams(
            implementation = Device_supplier_code.class
    )
    @ApiResponses
    @SLog("修改Device_supplier_code:${deviceSupplierCode.name}")
    @SaCheckPermission("device.suppliercode.update")
    public Result<?> update(@Param("..") Device_supplier_code deviceSupplierCode, HttpServletRequest req) {
        deviceSupplierCode.setUpdatedBy(SecurityUtil.getUserId());
        deviceSupplierCodeService.updateIgnoreNull(deviceSupplierCode);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_supplier_code")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.suppliercode")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_supplier_code deviceSupplierCode = deviceSupplierCodeService.fetch(id);
        if (deviceSupplierCode == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceSupplierCode);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_supplier_code")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_supplier_code:")
    @SaCheckPermission("device.suppliercode.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_supplier_code deviceSupplierCode = deviceSupplierCodeService.fetch(id);
        if (deviceSupplierCode == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceSupplierCodeService.delete(id);
        req.setAttribute("_slog_msg", deviceSupplierCode.getId());
        return Result.success();
    }
}