package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_info_state;
import com.budwk.app.device.services.DeviceInfoStateService;
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
@At("/admin/infostate")
@SLog(tag = "Device_info_state")
@ApiDefinition(tag = "Device_info_state")
@Slf4j
public class DeviceInfoStateController {
    @Inject
    private DeviceInfoStateService deviceInfoStateService;

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
    @SaCheckPermission("device.infostate")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceInfoStateService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_info_state")
    @ApiFormParams(
            implementation = Device_info_state.class
    )
    @ApiResponses
    @SLog("新增Device_info_state:${deviceInfoState.id}")
    @SaCheckPermission("device.infostate.create")
    public Result<?> create(@Param("..") Device_info_state deviceInfoState, HttpServletRequest req) {
        deviceInfoState.setCreatedBy(SecurityUtil.getUserId());
        deviceInfoStateService.insert(deviceInfoState);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_info_state")
    @ApiFormParams(
            implementation = Device_info_state.class
    )
    @ApiResponses
    @SLog("修改Device_info_state:${deviceInfoState.name}")
    @SaCheckPermission("device.infostate.update")
    public Result<?> update(@Param("..") Device_info_state deviceInfoState, HttpServletRequest req) {
        deviceInfoState.setUpdatedBy(SecurityUtil.getUserId());
        deviceInfoStateService.updateIgnoreNull(deviceInfoState);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_info_state")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.infostate")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_info_state deviceInfoState = deviceInfoStateService.fetch(id);
        if (deviceInfoState == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceInfoState);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_info_state")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_info_state:")
    @SaCheckPermission("device.infostate.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_info_state deviceInfoState = deviceInfoStateService.fetch(id);
        if (deviceInfoState == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceInfoStateService.delete(id);
        req.setAttribute("_slog_msg", deviceInfoState.getDeviceId());
        return Result.success();
    }
}