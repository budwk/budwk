package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_product_attr;
import com.budwk.app.device.services.DeviceProductAttrService;
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
@At("/admin/productattr")
@SLog(tag = "Device_product_attr")
@ApiDefinition(tag = "Device_product_attr")
@Slf4j
public class DeviceProductAttrController {
    @Inject
    private DeviceProductAttrService deviceProductAttrService;

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
    @SaCheckPermission("device.productattr")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceProductAttrService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_product_attr")
    @ApiFormParams(
            implementation = Device_product_attr.class
    )
    @ApiResponses
    @SLog("新增Device_product_attr:${deviceProductAttr.id}")
    @SaCheckPermission("device.productattr.create")
    public Result<?> create(@Param("..") Device_product_attr deviceProductAttr, HttpServletRequest req) {
        deviceProductAttr.setCreatedBy(SecurityUtil.getUserId());
        deviceProductAttrService.insert(deviceProductAttr);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_product_attr")
    @ApiFormParams(
            implementation = Device_product_attr.class
    )
    @ApiResponses
    @SLog("修改Device_product_attr:${deviceProductAttr.name}")
    @SaCheckPermission("device.productattr.update")
    public Result<?> update(@Param("..") Device_product_attr deviceProductAttr, HttpServletRequest req) {
        deviceProductAttr.setUpdatedBy(SecurityUtil.getUserId());
        deviceProductAttrService.updateIgnoreNull(deviceProductAttr);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_product_attr")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.productattr")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_product_attr deviceProductAttr = deviceProductAttrService.fetch(id);
        if (deviceProductAttr == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceProductAttr);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_product_attr")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_product_attr:")
    @SaCheckPermission("device.productattr.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_product_attr deviceProductAttr = deviceProductAttrService.fetch(id);
        if (deviceProductAttr == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceProductAttrService.delete(id);
        req.setAttribute("_slog_msg", deviceProductAttr.getId());
        return Result.success();
    }
}