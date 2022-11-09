package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_user_security;
import com.budwk.app.sys.services.SysUserSecurityService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/security")
@SLog(tag = "账户安全")
@ApiDefinition(tag = "账户安全")
@Slf4j
public class SysSecurityController {
    @Inject
    private SysUserSecurityService sysUserSecurityService;

    @At("/save")
    @Ok("json")
    @POST
    @SLog(value = "保存账户安全配置")
    @ApiOperation(name = "保存账户安全配置")
    @ApiFormParams(
            implementation = Sys_user_security.class
    )
    @ApiResponses
    @SaCheckPermission("sys.config.security.save")
    public Result<?> save(@Param("..") Sys_user_security security) {
        security.setCreatedBy(SecurityUtil.getUserId());
        security.setUpdatedBy(SecurityUtil.getUserId());
        sysUserSecurityService.insertOrUpdate(security);
        return Result.success();
    }

    @At("/get")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.config.security")
    @ApiOperation(name = "获取账户安全配置")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> getData(HttpServletRequest req) {
        return Result.data(sysUserSecurityService.fetch("MAIN"));
    }
}
