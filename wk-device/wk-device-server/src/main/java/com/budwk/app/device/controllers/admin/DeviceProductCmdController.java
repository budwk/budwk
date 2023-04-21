package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_product_cmd;
import com.budwk.app.device.services.DeviceProductCmdService;
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
@At("/admin/productcmd")
@SLog(tag = "Device_product_cmd")
@ApiDefinition(tag = "Device_product_cmd")
@Slf4j
public class DeviceProductCmdController {
    @Inject
    private DeviceProductCmdService deviceProductCmdService;

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
    @SaCheckPermission("device.productcmd")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceProductCmdService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_product_cmd")
    @ApiFormParams(
            implementation = Device_product_cmd.class
    )
    @ApiResponses
    @SLog("新增Device_product_cmd:${deviceProductCmd.id}")
    @SaCheckPermission("device.productcmd.create")
    public Result<?> create(@Param("..") Device_product_cmd deviceProductCmd, HttpServletRequest req) {
        deviceProductCmd.setCreatedBy(SecurityUtil.getUserId());
        deviceProductCmdService.insert(deviceProductCmd);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_product_cmd")
    @ApiFormParams(
            implementation = Device_product_cmd.class
    )
    @ApiResponses
    @SLog("修改Device_product_cmd:${deviceProductCmd.name}")
    @SaCheckPermission("device.productcmd.update")
    public Result<?> update(@Param("..") Device_product_cmd deviceProductCmd, HttpServletRequest req) {
        deviceProductCmd.setUpdatedBy(SecurityUtil.getUserId());
        deviceProductCmdService.updateIgnoreNull(deviceProductCmd);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_product_cmd")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.productcmd")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_product_cmd deviceProductCmd = deviceProductCmdService.fetch(id);
        if (deviceProductCmd == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceProductCmd);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_product_cmd")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_product_cmd:")
    @SaCheckPermission("device.productcmd.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_product_cmd deviceProductCmd = deviceProductCmdService.fetch(id);
        if (deviceProductCmd == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceProductCmdService.delete(id);
        req.setAttribute("_slog_msg", deviceProductCmd.getId());
        return Result.success();
    }
}