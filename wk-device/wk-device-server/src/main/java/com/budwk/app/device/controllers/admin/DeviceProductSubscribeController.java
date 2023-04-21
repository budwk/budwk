package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_product_subscribe;
import com.budwk.app.device.services.DeviceProductSubscribeService;
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
@At("/admin/productsubscribe")
@SLog(tag = "Device_product_subscribe")
@ApiDefinition(tag = "Device_product_subscribe")
@Slf4j
public class DeviceProductSubscribeController {
    @Inject
    private DeviceProductSubscribeService deviceProductSubscribeService;

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
    @SaCheckPermission("device.productsubscribe")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceProductSubscribeService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_product_subscribe")
    @ApiFormParams(
            implementation = Device_product_subscribe.class
    )
    @ApiResponses
    @SLog("新增Device_product_subscribe:${deviceProductSubscribe.id}")
    @SaCheckPermission("device.productsubscribe.create")
    public Result<?> create(@Param("..") Device_product_subscribe deviceProductSubscribe, HttpServletRequest req) {
        deviceProductSubscribe.setCreatedBy(SecurityUtil.getUserId());
        deviceProductSubscribeService.insert(deviceProductSubscribe);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_product_subscribe")
    @ApiFormParams(
            implementation = Device_product_subscribe.class
    )
    @ApiResponses
    @SLog("修改Device_product_subscribe:${deviceProductSubscribe.name}")
    @SaCheckPermission("device.productsubscribe.update")
    public Result<?> update(@Param("..") Device_product_subscribe deviceProductSubscribe, HttpServletRequest req) {
        deviceProductSubscribe.setUpdatedBy(SecurityUtil.getUserId());
        deviceProductSubscribeService.updateIgnoreNull(deviceProductSubscribe);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_product_subscribe")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.productsubscribe")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_product_subscribe deviceProductSubscribe = deviceProductSubscribeService.fetch(id);
        if (deviceProductSubscribe == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceProductSubscribe);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_product_subscribe")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_product_subscribe:")
    @SaCheckPermission("device.productsubscribe.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_product_subscribe deviceProductSubscribe = deviceProductSubscribeService.fetch(id);
        if (deviceProductSubscribe == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceProductSubscribeService.delete(id);
        req.setAttribute("_slog_msg", deviceProductSubscribe.getId());
        return Result.success();
    }
}