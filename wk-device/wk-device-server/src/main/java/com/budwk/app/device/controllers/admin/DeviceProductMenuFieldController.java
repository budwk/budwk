package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_product_menu_field;
import com.budwk.app.device.services.DeviceProductMenuFieldService;
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
@At("/admin/productmenufield")
@SLog(tag = "Device_product_menu_field")
@ApiDefinition(tag = "Device_product_menu_field")
@Slf4j
public class DeviceProductMenuFieldController {
    @Inject
    private DeviceProductMenuFieldService deviceProductMenuFieldService;

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
    @SaCheckPermission("device.productmenufield")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceProductMenuFieldService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_product_menu_field")
    @ApiFormParams(
            implementation = Device_product_menu_field.class
    )
    @ApiResponses
    @SLog("新增Device_product_menu_field:${deviceProductMenuField.id}")
    @SaCheckPermission("device.productmenufield.create")
    public Result<?> create(@Param("..") Device_product_menu_field deviceProductMenuField, HttpServletRequest req) {
        deviceProductMenuField.setCreatedBy(SecurityUtil.getUserId());
        deviceProductMenuFieldService.insert(deviceProductMenuField);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_product_menu_field")
    @ApiFormParams(
            implementation = Device_product_menu_field.class
    )
    @ApiResponses
    @SLog("修改Device_product_menu_field:${deviceProductMenuField.name}")
    @SaCheckPermission("device.productmenufield.update")
    public Result<?> update(@Param("..") Device_product_menu_field deviceProductMenuField, HttpServletRequest req) {
        deviceProductMenuField.setUpdatedBy(SecurityUtil.getUserId());
        deviceProductMenuFieldService.updateIgnoreNull(deviceProductMenuField);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_product_menu_field")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.productmenufield")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_product_menu_field deviceProductMenuField = deviceProductMenuFieldService.fetch(id);
        if (deviceProductMenuField == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceProductMenuField);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_product_menu_field")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_product_menu_field:")
    @SaCheckPermission("device.productmenufield.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_product_menu_field deviceProductMenuField = deviceProductMenuFieldService.fetch(id);
        if (deviceProductMenuField == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceProductMenuFieldService.delete(id);
        req.setAttribute("_slog_msg", deviceProductMenuField.getId());
        return Result.success();
    }
}