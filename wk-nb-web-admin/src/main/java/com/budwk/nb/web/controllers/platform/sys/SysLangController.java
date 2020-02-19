package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_lang;
import com.budwk.nb.sys.models.Sys_lang_local;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/11/15
 */
@IocBean
@At("/api/{version}/platform/sys/lang")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_多语言管理")}, servers = @Server(url = "/"))
public class SysLangController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysLangService sysLangService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;

    @At("/locale")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.config.lang")
    @Operation(
            tags = "系统_多语言管理", summary = "查询语言",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object locale() {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.asc("location");
            return Result.success().addData(sysLangLocalService.query(cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/get")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang")
    @Operation(
            tags = "系统_多语言管理", summary = "通过KEY查询多语言字符串",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang")
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
                    @ApiFormParam(name = "lang_key",description = "多语言KEY")
            }
    )
    public Object getLang(@Param("lang_key") String lang_key) {
        try {
            NutMap nutMap = NutMap.NEW();
            nutMap.addv("lang_key", lang_key);
            NutMap resMap = NutMap.NEW();
            List<Sys_lang> langList = sysLangService.query(Cnd.where("lang_key", "=", lang_key));
            for (Sys_lang lang : langList) {
                resMap.addv(lang.getLocale(), lang.getLang_value());
            }
            nutMap.addv("lang_value", resMap);
            return Result.success().addData(nutMap);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/create_or_update")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.create")
    @Operation(
            tags = "系统_多语言管理", summary = "创建或修改多语言字符串",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang.create")
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
                    @ApiFormParam(name = "lang_key", description = "多语言字符串KEY", example = "sys.test"),
                    @ApiFormParam(name = "lang_value[zh-CN]", description = "多语言字符串", example = "中文"),
                    @ApiFormParam(name = "lang_value[en-US]", description = "多语言字符串", example = "英文")
            }
    )
    public Object createOrUpdate(@Param("lang_key") String lang_key, HttpServletRequest req) {
        try {
            List<Sys_lang_local> list = sysLangLocalService.query();
            for (Sys_lang_local local : list) {
                String lang_value = Strings.sNull(req.getParameter("lang_value[" + local.getLocale() + "]"));
                int num = sysLangService.count(Cnd.where("lang_key", "=", lang_key).and("locale", "=", local.getLocale()));
                if (num > 0) {
                    sysLangService.update(Chain.make("lang_value", lang_value), Cnd.where("lang_key", "=", lang_key).and("locale", "=", local.getLocale()));
                } else {
                    Sys_lang sysLang = new Sys_lang();
                    sysLang.setLocale(local.getLocale());
                    sysLang.setLang_key(lang_key);
                    sysLang.setLang_value(lang_value);
                    sysLang.setCreatedBy(StringUtil.getPlatformUid());
                    sysLang.setUpdatedBy(StringUtil.getPlatformUid());
                    sysLangService.insert(sysLang);
                }
            }
            sysLangService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.config.lang")
    @Operation(
            tags = "系统_多语言管理", summary = "分页查询多语言字符串",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang")
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
                    @ApiFormParam(name = "locale", example = "zh-CN", description = "语言标识"),
                    @ApiFormParam(name = "lang_key", example = "", description = "字符串KEY"),
                    @ApiFormParam(name = "lang_value", example = "", description = "字符串内容"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("locale") String locale, @Param("lang_key") String lang_key, @Param("lang_value") String lang_value, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(locale)) {
                cnd.and("locale", "=", locale);
            }
            if (Strings.isNotBlank(lang_key)) {
                cnd.and(Cnd.likeEX("lang_key", lang_key));
            }
            if (Strings.isNotBlank(lang_value)) {
                cnd.and(Cnd.likeEX("lang_value", lang_value));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysLangService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/language_create")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.create")
    @Operation(
            tags = "系统_多语言管理", summary = "添加语言",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang.create")
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
                    @ApiFormParam(name = "name", description = "语言名称"),
                    @ApiFormParam(name = "locale", description = "语言标识")
            }
    )
    public Object languageCreate(@Param("name") String name, @Param("locale") String locale, HttpServletRequest req) {
        try {
            if (sysLangLocalService.count(Cnd.where("locale", "=", locale)) > 0) {
                return Result.error("sys.lang.error.localeExist");
            }
            Sys_lang_local local = new Sys_lang_local();
            local.setLocale(locale);
            local.setName(name);
            local.setCreatedBy(StringUtil.getPlatformUid());
            local.setUpdatedBy(StringUtil.getPlatformUid());
            sysLangLocalService.insert(local);
            sysLangLocalService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/language_delete")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.delete")
    @Operation(
            tags = "系统_多语言管理", summary = "删除语言",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang.delete")
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
                    @ApiFormParam(name = "locale", description = "语言标识")
            }
    )
    public Object languageDelete(@Param("locale") String locale, HttpServletRequest req) {
        try {
            sysLangLocalService.clearLocal(locale);
            sysLangService.clearCache();
            sysLangLocalService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/delete")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.delete")
    @Operation(
            tags = "系统_多语言管理", summary = "删除多语言字符串",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.lang.delete")
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
                    @ApiFormParam(name = "lang_key", description = "多语言字符串KEY")
            }
    )
    public Object deleteLang(@Param("lang_key") String lang_key, HttpServletRequest req) {
        try {
            sysLangService.clear(Cnd.where("lang_key", "=", lang_key));
            sysLangService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
