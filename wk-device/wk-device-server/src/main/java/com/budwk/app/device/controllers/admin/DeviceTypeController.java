package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.enums.DeviceType;
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
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author wizzer.cn
 */
@IocBean
@At("/admin/devtype")
@SLog(tag = "设备类型")
@ApiDefinition(tag = "设备类型")
@Slf4j
public class DeviceTypeController {
    @Inject
    private DeviceTypeService deviceTypeService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取所需数据")
    @ApiImplicitParams
    @ApiResponses(
            implementation = DeviceType.class
    )
    @SaCheckLogin
    public Result<?> init() {
        return Result.success().addData(NutMap.NEW().addv("DeviceType", DeviceType.values()));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "列表查询")
    @ApiImplicitParams
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("device.settings.devtype")
    public Result<?> list(@Param("name") String name) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(deviceTypeService.query(cnd));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增设备类型")
    @ApiFormParams(
            implementation = Device_type.class
    )
    @ApiResponses
    @SLog("新增设备类型:${deviceType.name}")
    @SaCheckPermission("device.settings.devtype.create")
    public Result<?> create(@Param("..") Device_type deviceType,@Param("parentId") String parentId, HttpServletRequest req) {
        deviceType.setCreatedBy(SecurityUtil.getUserId());
        deviceTypeService.save(deviceType, parentId);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改设备类型")
    @ApiFormParams(
            implementation = Device_type.class
    )
    @ApiResponses
    @SLog("修改设备类型:${deviceType.name}")
    @SaCheckPermission("device.settings.devtype.update")
    public Result<?> update(@Param("..") Device_type deviceType, HttpServletRequest req) {
        deviceType.setUpdatedBy(SecurityUtil.getUserId());
        deviceTypeService.updateIgnoreNull(deviceType);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取设备类型")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.settings.devtype")
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
    @ApiOperation(name = "删除设备类型")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除设备类型:")
    @SaCheckPermission("device.settings.devtype.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_type deviceType = deviceTypeService.fetch(id);
        if (deviceType == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceTypeService.delete(id);
        req.setAttribute("_slog_msg", deviceType.getName());
        return Result.success();
    }
}