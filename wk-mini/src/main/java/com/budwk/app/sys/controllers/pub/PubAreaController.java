package com.budwk.app.sys.controllers.pub;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.budwk.app.sys.services.SysAreaService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer.cn
 */
@IocBean
@At("/platform/pub/area")
@SLog(tag = "文件服务")
@ApiDefinition(tag = "文件服务")
@Slf4j
public class PubAreaController {
    @Inject
    private SysAreaService sysAreaService;

    @At("/list")
    @Ok("json")
    @GET
    @SaCheckLogin
    @ApiOperation(name = "获取下级行政区划列表")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "code", description = "行政编码(不传则返回第一级)")
            }
    )
    @ApiResponses
    public Result<?> getList(@Param("code") String code, HttpServletRequest req) {
        return Result.data(sysAreaService.getSubListByCode(code));
    }

    @At("/map")
    @Ok("json")
    @GET
    @SaCheckLogin
    @ApiOperation(name = "获取下级行政区划Map")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "code", description = "行政编码(不传则返回第一级)")
            }
    )
    @ApiResponses
    public Result<?> getMap(@Param("code") String code, HttpServletRequest req) {
        return Result.data(sysAreaService.getSubMapByCode(code));
    }
}
