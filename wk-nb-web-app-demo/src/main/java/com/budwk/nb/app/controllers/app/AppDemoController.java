package com.budwk.nb.app.controllers.app;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.app.commons.shiro.service.JwtService;
import com.budwk.nb.app.commons.utils.JwtUtil;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysUserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/app/demo")
@Ok("json")
@OpenAPIDefinition(tags = {@Tag(name = "APP_小程序_演示")}, servers = @Server(url = "/"))
public class AppDemoController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    private JwtService jwtService;

    @At("/login")
    @POST
    @Ok("json:{locked:'password|salt'}")
    @Operation(
            tags = "商城_会员登陆", summary = "注册及登陆",
            security = {
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "code", example = "", description = "js_code")
            },
            mediaType = "application/json"
    )
    @AdaptBy(type = JsonAdaptor.class)
    public Object doWxlogin(@Param("loginname") String loginname, @Param("password") String password) {
        try {
            //用户密码及验证码验证略
            Sys_user sysUser = null;//注意实际不是null啊!!!
            String token = jwtService.loginForToken(sysUser);

            JwtUtil.getLoginname();//登录后其他控制类可通过此方法获取登陆名
            JwtUtil.getUId();//登录后其他控制类可通过此方法获取UID
            return Result.success().addData(NutMap.NEW().addv("token", token).addv("userInfo", sysUser));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
