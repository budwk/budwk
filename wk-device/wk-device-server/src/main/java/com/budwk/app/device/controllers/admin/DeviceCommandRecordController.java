package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.services.DeviceCommandRecordService;
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
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author wizzer.cn
 */
@IocBean
@At("/admin/commandrecord")
@SLog(tag = "Device_command_record")
@ApiDefinition(tag = "Device_command_record")
@Slf4j
public class DeviceCommandRecordController {
    @Inject
    private DeviceCommandRecordService deviceCommandRecordService;

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
    @SaCheckPermission("device.commandrecord")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceCommandRecordService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_command_record")
    @ApiFormParams(
            implementation = Device_cmd_record.class
    )
    @ApiResponses
    @SLog("新增Device_command_record:${deviceCommandRecord.id}")
    @SaCheckPermission("device.commandrecord.create")
    public Result<?> create(@Param("..") Device_cmd_record deviceCommandRecord, HttpServletRequest req) {
        deviceCommandRecord.setCreatedBy(SecurityUtil.getUserId());
        deviceCommandRecordService.insert(deviceCommandRecord);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_command_record")
    @ApiFormParams(
            implementation = Device_cmd_record.class
    )
    @ApiResponses
    @SLog("修改Device_command_record:${deviceCommandRecord.name}")
    @SaCheckPermission("device.commandrecord.update")
    public Result<?> update(@Param("..") Device_cmd_record deviceCommandRecord, HttpServletRequest req) {
        deviceCommandRecord.setUpdatedBy(SecurityUtil.getUserId());
        deviceCommandRecordService.updateIgnoreNull(deviceCommandRecord);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_command_record")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.commandrecord")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_cmd_record deviceCommandRecord = deviceCommandRecordService.fetch(id);
        if (deviceCommandRecord == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceCommandRecord);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_command_record")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_command_record:")
    @SaCheckPermission("device.commandrecord.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_cmd_record deviceCommandRecord = deviceCommandRecordService.fetch(id);
        if (deviceCommandRecord == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceCommandRecordService.delete(id);
        req.setAttribute("_slog_msg", deviceCommandRecord.getId());
        return Result.success();
    }
}