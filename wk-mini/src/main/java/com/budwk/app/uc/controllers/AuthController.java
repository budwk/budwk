package com.budwk.app.uc.controllers;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.SysConfigService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.app.uc.services.AuthService;
import com.budwk.app.uc.services.ValidateService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.security.utils.SecurityUtil;
import com.budwk.starter.sms.enums.SmsType;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/ucenter/auth")
@SLog(tag = "用户认证")
@ApiDefinition(tag = "用户认证")
@Slf4j
public class AuthController {

    @Inject
    private AuthService authService;

    @Inject
    private ValidateService validateService;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysConfigService sysConfigService;

    @At
    @Ok("json")
    @POST
    @SLog(type = LogType.LOGIN, value = "登录方式: ${type=='password'?'登录密码':'短信验证码'}")
    @ApiOperation(name = "用户登录", description = "密码登录或短信验证码登录")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "loginname", description = "用户名"),
                    @ApiFormParam(name = "password", description = "登录密码"),
                    @ApiFormParam(name = "mobile", description = "手机号码", check = true, validation = Validation.MOBILE),
                    @ApiFormParam(name = "type", description = "登录方式", example = "mobile-短信验证码登录/password-用户名密码登录"),
                    @ApiFormParam(name = "captchaKey", description = "验证码Key"),
                    @ApiFormParam(name = "captchaCode", description = "验证码"),
                    @ApiFormParam(name = "smscode", description = "短信验证码"),
                    @ApiFormParam(name = "appId", description = "当前登录的应用ID")
            }
    )
    @ApiResponses
    public Result<?> login(@Param("loginname") String loginname,
                           @Param("password") String password,
                           @Param("mobile") String mobile,
                           @Param("type") String type,
                           @Param("captchaKey") String captchaKey,
                           @Param("captchaCode") String captchaCode,
                           @Param("smscode") String smscode,
                           @Param("appId") String appId, HttpServletRequest req) {
        Sys_user user = null;
        if ("password".equalsIgnoreCase(type)) {
            authService.checkLoginname(loginname);
            user = authService.loginByPassword(loginname, password, captchaKey, captchaCode);
        } else if ("mobile".equalsIgnoreCase(type)) {
            authService.checkMobile(mobile);
            user = authService.loginByMobile(mobile, smscode);
        } else {
            throw new BaseException("请求方式不正确");
        }
        if (user == null) {
            throw new BaseException("用户登录失败");
        }
        StpUtil.login(user.getId());
        StpUtil.checkLogin();
        sysUserService.setLoginInfo(user.getId(), Lang.getIP(req));
        SaSession session = StpUtil.getSession(true);
        session.set("loginname", Strings.sNull(user.getLoginname()));
        session.set("username", Strings.sNull(user.getUsername()));
        session.set("appId", Strings.sNull(appId));
        session.set("unitId", Strings.sNull(user.getUnitId()));
        session.set("unitPath", Strings.sNull(user.getUnitPath()));
        return Result.success().addData(StpUtil.getTokenInfo().getTokenValue());
    }

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取验证码", description = "图形验证码")
    @ApiImplicitParams
    @ApiResponses(
            {
                    @ApiResponse(name = "key", description = "验证码的一次性key"),
                    @ApiResponse(name = "code", description = "验证码值")
            }
    )
    public Result<?> captcha() {
        return validateService.getCaptcha();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "获取短信验证码", description = "发送短信验证码")
    @ApiFormParams({
            @ApiFormParam(name = "mobile", description = "手机号码", required = true, check = true, validation = Validation.MOBILE)
    })
    @ApiResponses
    public Result<?> smscode(@Param("mobile") String mobile) throws BaseException {
        authService.checkMobile(mobile);
        validateService.getSmsCode(mobile, SmsType.LOGIN);
        return Result.success();
    }

    @At("/check/loginname")
    @Ok("json")
    @POST
    @ApiOperation(name = "检查用户名是否存在")
    @ApiFormParams({
            @ApiFormParam(name = "loginname", description = "用户名", required = true, check = true)
    })
    @ApiResponses
    public Result<?> checkLoginname(@Param("loginname") String loginname) throws BaseException {
        Sys_user user = authService.getUserByLoginname(loginname);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        List<NutMap> typeList = new ArrayList<>();
        if (Strings.isNotBlank(user.getMobile())) {
            typeList.add(NutMap.NEW().addv("key", "mobile").addv("val", "手机号码"));
        }
        if (Strings.isNotBlank(user.getEmail())) {
            typeList.add(NutMap.NEW().addv("key", "email").addv("val", "电子邮箱"));
        }
        return Result.success().addData(typeList);
    }

    @At("/pwd/sendcode")
    @Ok("json")
    @POST
    @ApiOperation(name = "发送重置密码验证码", description = "type=mobile 短信接收验证码,type=email 邮箱接收验证码")
    @ApiFormParams({
            @ApiFormParam(name = "loginname", description = "用户名", required = true, check = true),
            @ApiFormParam(name = "type", description = "验证方式", required = true, check = true)
    })
    @ApiResponses
    public Result<?> pwdSendCode(@Param("loginname") String loginname, @Param("type") String type) throws BaseException {
        Sys_user user = authService.getUserByLoginname(loginname);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        String msg = "";
        if ("mobile".equalsIgnoreCase(type)) {
            validateService.getSmsCode(user.getMobile(), SmsType.PASSWORD);
            msg = "验证码已发送至手机号码，请注意查收";
        }
        if ("email".equalsIgnoreCase(type)) {
            validateService.getEmailCode("您正在重置密码", Strings.trim(loginname), user.getEmail());
            msg = "验证码已发送至Email，请注意查收";
        }
        return Result.success().addMsg(msg);
    }

    @At("/pwd/save")
    @Ok("json")
    @POST
    @SLog(value = "重置密码")
    @ApiOperation(name = "保存重置的新密码")
    @ApiFormParams({
            @ApiFormParam(name = "loginname", description = "用户名", required = true, check = true),
            @ApiFormParam(name = "type", description = "验证方式", required = true, check = true),
            @ApiFormParam(name = "password", description = "新密码", required = true, check = true, validation = Validation.PASSWORD),
            @ApiFormParam(name = "code", description = "验证码", required = true, check = true)
    })
    @ApiResponses
    public Result<?> pwdSave(@Param("loginname") String loginname, @Param("type") String type,
                             @Param("password") String password, @Param("code") String code) throws BaseException {
        Sys_user user = authService.getUserByLoginname(loginname);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        if ("mobile".equalsIgnoreCase(type)) {
            validateService.checkSMSCode(user.getMobile(), code);
        } else if ("email".equalsIgnoreCase(type)) {
            validateService.checkEmailCode(Strings.trim(loginname), code);
        } else {
            throw new BaseException("验证方式不正确");
        }
        authService.setPwdByLoginname(loginname, password);
        return Result.success();
    }


    @At
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @ApiOperation(name = "获取登录用户信息", description = "需登录成功")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "appId", description = "应用ID", required = true, check = true)
            }
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "conf", description = "系统参数", type = "object"),
                    @ApiResponse(name = "user", description = "用户信息", type = "object"),
                    @ApiResponse(name = "token", description = "Token值", type = "object"),
                    @ApiResponse(name = "menus", description = "权限菜单", type = "object"),
                    @ApiResponse(name = "roles", description = "角色列表", type = "object")
            }
    )
    @SaCheckLogin
    public Result<?> info(@Param("appId") String appId) {
        SecurityUtil.setAppId(appId);
        Sys_user user = authService.getUserById(StpUtil.getLoginIdAsString());
        NutMap map = NutMap.NEW();
        //获取应用菜单及公共菜单
        map.addv(SecurityUtil.getAppId(), sysUserService.getMenuList(SecurityUtil.getUserId(), appId));
        map.addv(GlobalConstant.DEFAULT_COMMON_APPID, sysUserService.getMenuList(SecurityUtil.getUserId(), GlobalConstant.DEFAULT_COMMON_APPID));
        return Result.success().addData(NutMap.NEW()
                .addv("conf", sysConfigService.getMapAll(SecurityUtil.getAppId()))
                .addv("user", user)
                .addv("token", StpUtil.getTokenInfo())
                .addv("apps", sysUserService.getAppList(SecurityUtil.getUserId()))
                .addv("menus", map)
                .addv("roles", sysUserService.getRoleList(SecurityUtil.getUserId())));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "设置自定义布局")
    @ApiFormParams({
            @ApiFormParam(name = "themeConfig", description = "配置内容", example = "{}")
    })
    @ApiResponses
    public Result<?> theme(@Param("themeConfig") String themeConfig) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        sysUserService.setThemeConfig(SecurityUtil.getUserId(), themeConfig);
        return Result.success();
    }

    @At
    @GET
    @SLog(type = LogType.LOGIN, value = "退出登录")
    @Ok("json")
    @ApiOperation(name = "退出登录")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckLogin
    public Result<?> logout() {
        StpUtil.logout();
        return Result.success();
    }
}
