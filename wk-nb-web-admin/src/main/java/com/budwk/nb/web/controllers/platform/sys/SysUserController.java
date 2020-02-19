package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysUnitService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumn;
import org.nutz.integration.json4excel.J4EConf;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/11/21
 */
@IocBean
@At("/api/{version}/platform/sys/user")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_用户管理")}, servers = @Server(url = "/"))
public class SysUserController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    @Reference
    private SysUnitService sysUnitService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;
    @Inject
    @Reference
    private ShiroUtil shiroUtil;
    @Inject
    private RedisService redisService;

    @At("/logon_user_info")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "获取已登陆用户数据", description = "返回语言,系统参数,用户信息,角色,菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content =
                            @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"lang\": [\n" +
                                    "      {\n" +
                                    "        \"name\": \"中文\",\n" +
                                    "        \"locale\": \"zh-CN\",\n" +
                                    "        \"disabled\": false\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"name\": \"English\",\n" +
                                    "        \"locale\": \"en-US\",\n" +
                                    "        \"disabled\": false\n" +
                                    "      }\n" +
                                    "    ],\n" +
                                    "    \"conf\": {\n" +
                                    "      \"AppDemoEnv\": \"true\",\n" +
                                    "      \"AppDomain\": \"http://127.0.0.1:3999\",\n" +
                                    "      \"AppFileDomain\": \"http://127.0.0.1:9527\",\n" +
                                    "      \"AppName\": \"BudWK-V6\",\n" +
                                    "      \"AppShrotName\": \"BudWK\",\n" +
                                    "      \"AppUploadBase\": \"/upload\",\n" +
                                    "      \"AppWebBrowserNotice\": \"true\",\n" +
                                    "      \"AppWebSocket\": \"true\",\n" +
                                    "      \"AppWebUserOnlyOne\": \"true\"\n" +
                                    "    },\n" +
                                    "    \"user\": {\n" +
                                    "      \"id\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "      \"loginname\": \"superadmin\",\n" +
                                    "      \"username\": \"超级管理员\",\n" +
                                    "      \"avatar\": \"/upload/image/20191219/0vonuing0agadq7bcnf7aqntgu.jpg\",\n" +
                                    "      \"userOnline\": false,\n" +
                                    "      \"disabled\": false,\n" +
                                    "      \"email\": \"wizzer@qq.com\",\n" +
                                    "      \"mobile\": \"\",\n" +
                                    "      \"loginAt\": 1581753439688,\n" +
                                    "      \"loginIp\": \"127.0.0.1\",\n" +
                                    "      \"loginCount\": 189,\n" +
                                    "      \"customMenu\": null,\n" +
                                    "      \"themeConfig\": \"{\\\"theme\\\":\\\"theme1\\\",\\\"fixedHeader\\\":false,\\\"fixedAside\\\":false,\\\"verticalMenu\\\":true,\\\"contentWidth\\\":\\\"s\\\"}\",\n" +
                                    "      \"unitid\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "      \"unit\": {\n" +
                                    "        \"id\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "        \"parentId\": \"\",\n" +
                                    "        \"path\": \"0001\",\n" +
                                    "        \"name\": \"系统管理\",\n" +
                                    "        \"aliasName\": \"System\",\n" +
                                    "        \"unitcode\": null,\n" +
                                    "        \"note\": null,\n" +
                                    "        \"address\": \"银河-太阳系-地球\",\n" +
                                    "        \"telephone\": \"\",\n" +
                                    "        \"email\": \"wizzer@qq.com\",\n" +
                                    "        \"website\": \"https://wizzer.cn\",\n" +
                                    "        \"location\": 0,\n" +
                                    "        \"hasChildren\": false,\n" +
                                    "        \"logo\": null,\n" +
                                    "        \"createdBy\": \"\",\n" +
                                    "        \"createdAt\": 1573701338314,\n" +
                                    "        \"updatedBy\": \"\",\n" +
                                    "        \"updatedAt\": 1573701338314,\n" +
                                    "        \"delFlag\": false\n" +
                                    "      },\n" +
                                    "      \"createdByUser\": null,\n" +
                                    "      \"updatedByUser\": null,\n" +
                                    "      \"roles\": [\n" +
                                    "        {\n" +
                                    "          \"id\": \"9b59a5ebd4d844c6a15482f342d87c27\",\n" +
                                    "          \"name\": \"系统管理员\",\n" +
                                    "          \"code\": \"sysadmin\",\n" +
                                    "          \"disabled\": false,\n" +
                                    "          \"unitid\": \"\",\n" +
                                    "          \"note\": \"System Admin\",\n" +
                                    "          \"unit\": null,\n" +
                                    "          \"createdByUser\": null,\n" +
                                    "          \"menus\": null,\n" +
                                    "          \"users\": null,\n" +
                                    "          \"createdBy\": \"\",\n" +
                                    "          \"createdAt\": 1573701338840,\n" +
                                    "          \"updatedBy\": \"\",\n" +
                                    "          \"updatedAt\": 1573701338840,\n" +
                                    "          \"delFlag\": false\n" +
                                    "        }\n" +
                                    "      ],\n" +
                                    "      \"units\": [],\n" +
                                    "      \"createdBy\": \"d399be64462c40d992cf308871ccd3bb\",\n" +
                                    "      \"createdAt\": 1575969280648,\n" +
                                    "      \"updatedBy\": \"d399be64462c40d992cf308871ccd3bb\",\n" +
                                    "      \"updatedAt\": 1575969280648,\n" +
                                    "      \"delFlag\": false\n" +
                                    "    },\n" +
                                    "    \"token\": \"d9fba300fc548794db116f17fa60046c\",\n" +
                                    "    \"menus\": [\n" +
                                    "      {\n" +
                                    "        \"id\": \"7fbd82da6292478bb6da15e3c92ce52f\",\n" +
                                    "        \"parentId\": \"\",\n" +
                                    "        \"path\": \"0001\",\n" +
                                    "        \"name\": \"系统\",\n" +
                                    "        \"alias\": \"sys\",\n" +
                                    "        \"type\": \"menu\",\n" +
                                    "        \"href\": \"\",\n" +
                                    "        \"target\": \"\",\n" +
                                    "        \"icon\": \"fa fa-cogs\",\n" +
                                    "        \"showit\": true,\n" +
                                    "        \"disabled\": false,\n" +
                                    "        \"permission\": \"sys\",\n" +
                                    "        \"note\": \"系统\",\n" +
                                    "        \"location\": 0,\n" +
                                    "        \"hasChildren\": false,\n" +
                                    "        \"buttons\": null,\n" +
                                    "        \"createdBy\": \"\",\n" +
                                    "        \"createdAt\": 1573701338343,\n" +
                                    "        \"updatedBy\": \"a24156c5a59641bfb8f5113ec9118c54\",\n" +
                                    "        \"updatedAt\": 1574745422508,\n" +
                                    "        \"delFlag\": false\n" +
                                    "      }\n" +
                                    "    ],\n" +
                                    "    \"roles\": [\n" +
                                    "      {\n" +
                                    "        \"code\": \"sysadmin\",\n" +
                                    "        \"name\": \"系统管理员\"\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  }\n" +
                                    "}"), mediaType = "application/json"
                            )
                    )
            }
    )
    public Object getLogonUserInfo(HttpSession session) {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                Sys_user user = (Sys_user) subject.getPrincipal();
                if (user != null) {
                    return Result.success().addData(
                            NutMap.NEW()
                                    .addv("lang", sysLangLocalService.getLocal())
                                    .addv("conf", Globals.MyConfig)
                                    .addv("user", user)
                                    .addv("token", Strings.sNull(session.getAttribute("userToken")))
                                    .addv("menus", sysUserService.getMenusAndButtons(user.getId()))
                                    .addv("roles", sysUserService.getRoles(user)));
                }
            }
            return Result.error(500211, "system.login.norole");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }


    @At("/set_user_theme")
    @Ok("json")
    @POST
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "设置用户主题",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                    @ApiFormParam(name = "themeConfig", example = "{}", description = "主题JSON字符串", required = true)
            }
    )
    public Object setUserThemeConfig(@Param("themeConfig") String themeConfig) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            sysUserService.update(Chain.make("themeConfig", themeConfig), Cnd.where("id", "=", StringUtil.getPlatformUid()));
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            // 替换当前用户session里的值
            if (user != null) {
                user.setThemeConfig(themeConfig);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    @At("/set_user_avatar")
    @Ok("json")
    @POST
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "设置用户头像",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                    @ApiFormParam(name = "avatar", example = "/upload/image/avatar.png", description = "图片路径", required = true)
            }
    )
    public Object setUserAvatar(@Param("avatar") String avatar) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            sysUserService.update(Chain.make("avatar", avatar), Cnd.where("id", "=", StringUtil.getPlatformUid()));
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            // 替换当前用户session里的值
            if (user != null) {
                user.setAvatar(avatar);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }


    @At("/change_user_info")
    @Ok("json")
    @Operation(
            tags = "系统_用户管理", summary = "设置用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "username", description = "用户姓名或昵称", required = false, in = ParameterIn.QUERY),
                    @Parameter(name = "email", description = "Email", required = false, in = ParameterIn.QUERY),
                    @Parameter(name = "mobile", description = "手机号", required = false, in = ParameterIn.QUERY),
                    @Parameter(name = "avatar", description = "用户头像(URL)", required = false, in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object doChangeInfo(@Param("username") String username, @Param("email") String email, @Param("mobile") String mobile, @Param("avatar") String avatar, HttpServletRequest req) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            if (user != null) {
                if (Strings.isNotBlank(email)) {
                    if (sysUserService.count(Cnd.where("email", "=", email).and("id", "<>", user.getId())) > 0) {
                        return Result.error().addMsg("sys.user.error.emailExisted");
                    }
                }
                if (Strings.isNotBlank(mobile)) {
                    if (sysUserService.count(Cnd.where("mobile", "=", mobile).and("id", "<>", user.getId())) > 0) {
                        return Result.error().addMsg("sys.user.error.mobileExisted");
                    }
                }
                sysUserService.update(Chain.make("username", username)
                                .add("avatar", avatar)
                                .add("email", email)
                                .add("mobile", mobile)
                        , Cnd.where("id", "=", user.getId()));
                user.setUsername(username);
                user.setAvatar(avatar);
                user.setEmail(email);
                user.setMobile(mobile);
                sysUserService.deleteCache(user.getId());
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }


    @At("/change_user_pwd")
    @Ok("json")
    @POST
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "更改用户密码",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                    @ApiFormParam(name = "oldPassword", example = "", description = "原密码", required = true, format = "password", type = "string"),
                    @ApiFormParam(name = "newPassword", example = "", description = "新密码", required = true, format = "password", type = "string")
            }
    )
    public Object doChangePassword(@Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword, HttpServletRequest req) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            String old = new Sha256Hash(oldPassword, user.getSalt(), 1024).toHex();
            if (old.equals(user.getPassword())) {
                String salt = R.UU32();
                String hashedPasswordBase64 = new Sha256Hash(newPassword, ByteSource.Util.bytes(salt), 1024).toHex();
                // 替换当前用户session里的值
                user.setSalt(salt);
                // 替换当前用户session里的值
                user.setPassword(hashedPasswordBase64);
                sysUserService.update(Chain.make("salt", salt)
                                .add("password", hashedPasswordBase64)
                        , Cnd.where("id", "=", user.getId()));
                sysUserService.deleteCache(user.getId());
                return Result.success();
            } else {
                return Result.error().addMsg("sys.user.change.pwd.oldPwdError");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }


    @At("/info_by_id/{id}")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "通过用户ID获取用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getInfoByUserId(String userId) {
        try {
            Sys_user sysUser = sysUserService.fetch(userId);
            if (sysUser == null) {
                return Result.error().addMsg("sys.user.not.exist");
            }
            return Result.success().addData(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    @At("/info_by_loginname/{loginname}")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_用户管理", summary = "通过用户名获取用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "loginname", description = "用户名", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getInfoByLoginname(String loginname) {
        try {
            Sys_user sysUser = sysUserService.fetch(Cnd.where("loginname", "=", loginname));
            if (sysUser == null) {
                return Result.error().addMsg("sys.user.not.exist");
            }
            return Result.success().addData(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    @At
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.manage.user")
    @Operation(
            tags = "系统_用户管理", summary = "分页查询用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user")
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
                    @ApiFormParam(name = "unitid", example = "", description = "单位ID"),
                    @ApiFormParam(name = "loginname", example = "", description = "用户名"),
                    @ApiFormParam(name = "username", example = "", description = "姓名或昵称"),
                    @ApiFormParam(name = "mobile", example = "", description = "手机号"),
                    @ApiFormParam(name = "email", example = "", description = "Email"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("unitid")String unitid,String loginname, @Param("username") String username, @Param("mobile") String mobile, @Param("email") String email, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    cnd.and("unitid", "=", unitid);
                }
            } else {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    Sys_unit unit = sysUnitService.fetch(unitid);
                    if (unit == null || !unit.getPath().startsWith(user.getUnit().getPath())) {
                        //防止有人越级访问
                        return Result.error("system.error.invalid");
                    } else {
                        cnd.and("unitid", "=", unitid);
                    }
                } else {
                    cnd.and("unitid", "=", user.getUnitid());
                }
            }
            if (Strings.isNotBlank(username)) {
                cnd.and(Cnd.likeEX("username", username));
            }
            if (Strings.isNotBlank(loginname)) {
                cnd.and(Cnd.likeEX("loginname", loginname));
            }
            if (Strings.isNotBlank(email)) {
                cnd.and(Cnd.likeEX("email", email));
            }
            if (Strings.isNotBlank(mobile)) {
                cnd.and(Cnd.likeEX("mobile", mobile));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = sysUserService.listPageLinks(pageNo, pageSize, cnd, "^(unit|createdByUser|updatedByUser)$");
            for (Sys_user user : pagination.getList(Sys_user.class)) {
                user.setUserOnline(getUserOnlineStatus(user.getId()));
            }
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private boolean getUserOnlineStatus(String userId) {
        try {
            Set<String> set = redisService.keys(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + userId + ":*");
            return set.size() > 0;
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }


    @At("/get_unit_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    @Operation(
            tags = "系统_用户管理", summary = "获取单位树数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getUnitTree(HttpServletRequest req) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (user != null) {
                    cnd.and("path", "like", user.getUnit().getPath() + "%");
                } else {
                    cnd.and("1", "<>", 1);
                }
            }
            cnd.asc("location").asc("path");
            List<Sys_unit> list = sysUnitService.query(cnd);
            NutMap nutMap = NutMap.NEW();
            for (Sys_unit unit : list) {
                List<Sys_unit> list1 = nutMap.getList(unit.getParentId(), Sys_unit.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(unit);
                nutMap.put(Strings.sNull(unit.getParentId()), list1);
            }
            return Result.success().addData(getTree(nutMap, "", req));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("id", "root").addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.manage.user.form.alluser")).addv("leaf", true);
            treeList.add(root);
        }
        List<Sys_unit> subList = nutMap.getList(pid, Sys_unit.class);
        for (Sys_unit unit : subList) {
            NutMap map = Lang.obj2nutmap(unit);
            map.put("label", unit.getName());
            if (unit.isHasChildren() || (nutMap.get(unit.getId()) != null)) {
                map.put("children", getTree(nutMap, unit.getId(), req));
            }
            treeList.add(map);
        }
        return treeList;
    }


    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "启用禁用用户")
    @Operation(
            tags = "系统_用户管理", summary = "启用禁用用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.update")
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
                    @ApiFormParam(name = "id",description = "主键",required = true),
                    @ApiFormParam(name = "disabled",description = "启用禁用",required = true,example = "true",type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            int userCount = sysUserService.count(Cnd.where("delFlag", "=", false).and("disabled", "=", false));
            if (userCount == 1) {
                Sys_user user = sysUserService.fetch(id);
                if (user != null && PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME.equals(user.getLoginname()) && disabled) {
                    // 只有一个启用用户时,超级管理员不可禁用
                    return Result.error("sys.manage.user.superadmin.disable");
                }
            }
            int res = sysUserService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }

                sysUserService.clearCache();
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At
    @Ok("json")
    @RequiresPermissions("sys.manage.user.create")
    @SLog(tag = "新增用户")
    @Operation(
            tags = "系统_用户管理", summary = "新增用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_user.class
    )
    public Object create(@Param("..") Sys_user user, Errors errors, HttpServletRequest req) {
        try {
            if (errors.hasError()) {
                return Result.error(errors.getErrorsList().toString());
            }
            if (Strings.isNotBlank(user.getLoginname())) {
                int num = sysUserService.count(Cnd.where("loginname", "=", Strings.trim(user.getLoginname())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.loginname");
                }
            }
            if (Strings.isNotBlank(user.getMobile())) {
                int num = sysUserService.count(Cnd.where("mobile", "=", Strings.trim(user.getMobile())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.mobile");
                }
            }
            if (Strings.isNotBlank(user.getEmail())) {
                int num = sysUserService.count(Cnd.where("email", "=", Strings.trim(user.getEmail())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.email");
                }
            }
            String salt = R.UU32();
            user.setSalt(salt);
            user.setPassword(new Sha256Hash(user.getPassword(), ByteSource.Util.bytes(salt), 1024).toHex());
            user.setLoginCount(0);
            user.setCreatedBy(StringUtil.getPlatformUid());
            user.setUpdatedBy(StringUtil.getPlatformUid());
            sysUserService.insert(user);
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/get_menu/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    @Operation(
            tags = "系统_用户管理", summary = "获取用户权限菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", in = ParameterIn.PATH),
                    @Parameter(name = "pid", description = "父级ID", in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"id\": \"7fbd82da6292478bb6da15e3c92ce52f\",\n" +
                                    "      \"parentId\": \"\",\n" +
                                    "      \"path\": \"0001\",\n" +
                                    "      \"name\": \"系统\",\n" +
                                    "      \"alias\": \"sys\",\n" +
                                    "      \"type\": \"menu\",\n" +
                                    "      \"href\": \"\",\n" +
                                    "      \"target\": \"\",\n" +
                                    "      \"icon\": \"fa fa-cogs\",\n" +
                                    "      \"showit\": true,\n" +
                                    "      \"disabled\": false,\n" +
                                    "      \"permission\": \"sys\",\n" +
                                    "      \"note\": \"系统\",\n" +
                                    "      \"location\": 0,\n" +
                                    "      \"hasChildren\": true,\n" +
                                    "      \"createdBy\": \"\",\n" +
                                    "      \"createdAt\": 1573701338343,\n" +
                                    "      \"updatedBy\": \"a24156c5a59641bfb8f5113ec9118c54\",\n" +
                                    "      \"updatedAt\": 1574745422508,\n" +
                                    "      \"delFlag\": false,\n" +
                                    "      \"expanded\": false,\n" +
                                    "      \"children\": []\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"id\": \"02e86a61e99746bea34236ea73dd52a5\",\n" +
                                    "      \"parentId\": \"\",\n" +
                                    "      \"path\": \"0003\",\n" +
                                    "      \"name\": \"CMS\",\n" +
                                    "      \"alias\": \"cms\",\n" +
                                    "      \"type\": \"menu\",\n" +
                                    "      \"href\": \"\",\n" +
                                    "      \"target\": \"\",\n" +
                                    "      \"icon\": \"fa fa-internet-explorer\",\n" +
                                    "      \"showit\": true,\n" +
                                    "      \"disabled\": false,\n" +
                                    "      \"permission\": \"cms\",\n" +
                                    "      \"location\": 9,\n" +
                                    "      \"hasChildren\": true,\n" +
                                    "      \"updatedBy\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "      \"updatedAt\": 1581299734759,\n" +
                                    "      \"delFlag\": false,\n" +
                                    "      \"expanded\": false,\n" +
                                    "      \"children\": []\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object getMenu(String id, @Param("pid") String pid) {
        try {
            List<Sys_menu> list = sysUserService.getRoleMenus(id, pid);
            List<NutMap> treeList = new ArrayList<>();
            for (Sys_menu unit : list) {
                if (!unit.isHasChildren() && sysUserService.hasChildren(id, unit.getId())) {
                    unit.setHasChildren(true);
                }
                NutMap map = Lang.obj2nutmap(unit);
                map.addv("expanded", false);
                map.addv("children", new ArrayList<>());
                treeList.add(map);
            }
            return Result.success().addData(treeList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/reset_pwd/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "重置用户密码")
    @Operation(
            tags = "系统_用户管理", summary = "重置用户密码",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.update")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object resetPwd(String id, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(id);
            String salt = R.UU32();
            String pwd = R.captchaNumber(6);
            String hashedPasswordBase64 = new Sha256Hash(pwd, ByteSource.Util.bytes(salt), 1024).toHex();
            sysUserService.update(Chain.make("salt", salt)
                    .add("password", hashedPasswordBase64)
                    .add("updatedBy", StringUtil.getPlatformUid())
                    .add("updatedAt", Times.now().getTime()), Cnd.where("id", "=", id));
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success().addData(pwd);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    @Operation(
            tags = "系统_用户管理", summary = "获取用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(id);
            if (user == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(user);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "修改用户")
    @Operation(
            tags = "系统_用户管理", summary = "修改用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_user.class
    )
    public Object update(@Param("..") Sys_user user, HttpServletRequest req) {
        try {
            user.setUpdatedBy(StringUtil.getPlatformUid());
            sysUserService.updateIgnoreNull(user);
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.user.delete")
    @SLog(tag = "删除用户")
    @Operation(
            tags = "系统_用户管理", summary = "删除用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String userId, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(userId);
            if (user == null) {
                return Result.error("system.error.noData");
            }
            if (PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME.equals(user.getLoginname())) {
                // 超级管理员不可删除
                return Result.error("sys.manage.user.superadmin.delete");
            }
            sysUserService.deleteById(userId);
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.delete")
    @SLog(tag = "删除用户")
    @Operation(
            tags = "系统_用户管理", summary = "批量删除用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user.delete")
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
                    @ApiFormParam(name = "ids", example = "a,b", description = "用户ID数组", required = true),
                    @ApiFormParam(name = "names", example = "a,b", description = "用户名数组", required = true)
            }
    )
    public Object deleteMore(@Param("ids") String[] ids, @Param("names") String names, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            Sys_user user = sysUserService.fetch(Cnd.where("loginname", "=", PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME));
            for (String s : ids) {
                if (s.equals(user.getId())) {
                    // 超级管理员不可删除,但有多个用户时可以禁用
                    return Result.error("sys.manage.user.superadmin.delete");
                }
            }
            int userCount = sysUserService.count(Cnd.where("delFlag", "=", false));
            if (userCount == ids.length) {
                // 用户不可以全部删除
                return Result.error("sys.manage.user.not.deleteAll");
            }
            sysUserService.deleteByIds(ids);
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", names);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At
    @Ok("void")
    @GET
    @RequiresPermissions("sys.manage.user")
    @Operation(
            tags = "系统_用户管理", summary = "导出用户资料xls文件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.user")
            },
            parameters = {
                    @Parameter(name = "unitid", description = "单位ID", in = ParameterIn.QUERY),
                    @Parameter(name = "loginname", description = "用户名", in = ParameterIn.QUERY),
                    @Parameter(name = "username", description = "姓名昵称", in = ParameterIn.QUERY),
                    @Parameter(name = "mobile", description = "手机号", in = ParameterIn.QUERY),
                    @Parameter(name = "email", description = "Email", in = ParameterIn.QUERY),
                    @Parameter(name = "pageNo", description = "页码", example = "1", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "pageSize", description = "页大小", example = "10", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "pageOrderName", description = "排序字段", example = "loginname", in = ParameterIn.QUERY),
                    @Parameter(name = "pageOrderBy", description = "排序方式", example = "descending", in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(format = "binary"), mediaType = "application/shlnd.ms-excel"))
            }
    )
    public void export(@Param("unitid") String unitid, @Param("loginname") String loginname, @Param("username") String username,
                       @Param("mobile") String mobile, @Param("email") String email, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy, HttpServletRequest req, HttpServletResponse response) {
        try {
            J4EConf j4eConf = J4EConf.from(Sys_user.class);
            j4eConf.setUse2007(true);
            List<J4EColumn> jcols = j4eConf.getColumns();
            for (J4EColumn j4eColumn : jcols) {
                if ("updatedBy".equals(j4eColumn.getFieldName()) || "updatedAt".equals(j4eColumn.getFieldName()) || "createdBy".equals(j4eColumn.getFieldName()) || "createdAt".equals(j4eColumn.getFieldName()) || "delFlag".equals(j4eColumn.getFieldName())) {
                    j4eColumn.setIgnore(true);
                }
            }
            Cnd cnd = Cnd.NEW();
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    cnd.and("unitid", "=", unitid);
                }
            } else {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    Sys_unit unit = sysUnitService.fetch(unitid);
                    if (unit == null || !unit.getPath().startsWith(user.getUnit().getPath())) {
                        //防止有人越级访问
                        throw Lang.makeThrow(Mvcs.getMessage(req, "system.error.invalid"));
                    } else {
                        cnd.and("unitid", "=", unitid);
                    }
                } else {
                    cnd.and("unitid", "=", user.getUnitid());
                }
            }
            if (Strings.isNotBlank(username)) {
                cnd.and(Cnd.likeEX("username", username));
            }
            if (Strings.isNotBlank(loginname)) {
                cnd.and(Cnd.likeEX("loginname", loginname));
            }
            if (Strings.isNotBlank(email)) {
                cnd.and(Cnd.likeEX("email", email));
            }
            if (Strings.isNotBlank(mobile)) {
                cnd.and(Cnd.likeEX("mobile", mobile));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            OutputStream out = response.getOutputStream();
            response.setHeader("content-type", "application/shlnd.ms-excel;charset=utf-8");
            response.setHeader("content-disposition", "attachment; filename=" + new String("sys_user".getBytes(), "ISO-8859-1") + ".xlsx");
            J4E.toExcel(out, sysUserService.query(cnd), j4eConf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
