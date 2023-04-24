package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_info_latest_data;
import com.budwk.app.device.services.DeviceInfoLatestDataService;
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
@At("/admin/infolatestdata")
@SLog(tag = "Device_info_latest_data")
@ApiDefinition(tag = "Device_info_latest_data")
@Slf4j
public class DeviceInfoLatestDataController {
    @Inject
    private DeviceInfoLatestDataService deviceInfoLatestDataService;

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
    @SaCheckPermission("device.infolatestdata")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceInfoLatestDataService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_info_latest_data")
    @ApiFormParams(
            implementation = Device_info_latest_data.class
    )
    @ApiResponses
    @SLog("新增Device_info_latest_data:${deviceInfoLatestData.id}")
    @SaCheckPermission("device.infolatestdata.create")
    public Result<?> create(@Param("..") Device_info_latest_data deviceInfoLatestData, HttpServletRequest req) {
        deviceInfoLatestData.setCreatedBy(SecurityUtil.getUserId());
        deviceInfoLatestDataService.insert(deviceInfoLatestData);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_info_latest_data")
    @ApiFormParams(
            implementation = Device_info_latest_data.class
    )
    @ApiResponses
    @SLog("修改Device_info_latest_data:${deviceInfoLatestData.name}")
    @SaCheckPermission("device.infolatestdata.update")
    public Result<?> update(@Param("..") Device_info_latest_data deviceInfoLatestData, HttpServletRequest req) {
        deviceInfoLatestData.setUpdatedBy(SecurityUtil.getUserId());
        deviceInfoLatestDataService.updateIgnoreNull(deviceInfoLatestData);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_info_latest_data")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.infolatestdata")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_info_latest_data deviceInfoLatestData = deviceInfoLatestDataService.fetch(id);
        if (deviceInfoLatestData == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceInfoLatestData);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_info_latest_data")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_info_latest_data:")
    @SaCheckPermission("device.infolatestdata.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_info_latest_data deviceInfoLatestData = deviceInfoLatestDataService.fetch(id);
        if (deviceInfoLatestData == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceInfoLatestDataService.delete(id);
        req.setAttribute("_slog_msg", deviceInfoLatestData.getDeviceId());
        return Result.success();
    }
}