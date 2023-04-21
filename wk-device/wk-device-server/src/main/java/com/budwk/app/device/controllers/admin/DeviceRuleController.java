package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_rule;
import com.budwk.app.device.services.DeviceRuleService;
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
@At("/admin/rule")
@SLog(tag = "Device_rule")
@ApiDefinition(tag = "Device_rule")
@Slf4j
public class DeviceRuleController {
    @Inject
    private DeviceRuleService deviceRuleService;

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
    @SaCheckPermission("device.rule")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(deviceRuleService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增Device_rule")
    @ApiFormParams(
            implementation = Device_rule.class
    )
    @ApiResponses
    @SLog("新增Device_rule:${deviceRule.id}")
    @SaCheckPermission("device.rule.create")
    public Result<?> create(@Param("..") Device_rule deviceRule, HttpServletRequest req) {
        deviceRule.setCreatedBy(SecurityUtil.getUserId());
        deviceRuleService.insert(deviceRule);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改Device_rule")
    @ApiFormParams(
            implementation = Device_rule.class
    )
    @ApiResponses
    @SLog("修改Device_rule:${deviceRule.name}")
    @SaCheckPermission("device.rule.update")
    public Result<?> update(@Param("..") Device_rule deviceRule, HttpServletRequest req) {
        deviceRule.setUpdatedBy(SecurityUtil.getUserId());
        deviceRuleService.updateIgnoreNull(deviceRule);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_rule")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.rule")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_rule deviceRule = deviceRuleService.fetch(id);
        if (deviceRule == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceRule);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除Device_rule")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除Device_rule:")
    @SaCheckPermission("device.rule.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_rule deviceRule = deviceRuleService.fetch(id);
        if (deviceRule == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceRuleService.delete(id);
        req.setAttribute("_slog_msg", deviceRule.getId());
        return Result.success();
    }
}