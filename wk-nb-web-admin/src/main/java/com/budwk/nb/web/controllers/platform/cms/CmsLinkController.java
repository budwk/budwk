package com.budwk.nb.web.controllers.platform.cms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.cms.models.Cms_link;
import com.budwk.nb.cms.services.CmsLinkClassService;
import com.budwk.nb.cms.services.CmsLinkService;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
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
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/3
 */
@IocBean
@At("/api/{version}/platform/cms/links/link")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "CMS_链接管理")}, servers = {@Server(url = "/")})
public class CmsLinkController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private CmsLinkClassService cmsLinkClassService;

    @Inject
    @Reference(check = false)
    private CmsLinkService cmsLinkService;

    @At("/list_class")
    @GET
    @Ok("json:{actived:'code|msg|data|id|name',ignoreNull:true}")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_链接管理", summary = "获取分类列表",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                                    "      \"id\": \"xxxx\",\n" +
                                    "      \"name\": \"友情链接\",\n" +
                                    "      \"code\": \"links\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object listClass(HttpServletRequest req) {
        try {
            return Result.success().addData(cmsLinkClassService.query());
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("cms.links.link")
    @Operation(
            tags = "CMS_链接管理", summary = "分页查询链接",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.link")
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
                    @ApiFormParam(name = "classId", example = "", description = "分类ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("classId") String classId, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(classId)) {
                cnd.and("classId", "=", classId);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(cmsLinkService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.links.link.create")
    @SLog(tag = "新增链接", msg = "链接名称:${link.name}")
    @Operation(
            tags = "CMS_链接管理", summary = "新增链接",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.link.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_link.class
    )
    public Object create(@Param("..") Cms_link link, HttpServletRequest req) {
        try {
            link.setCreatedBy(StringUtil.getPlatformUid());
            link.setUpdatedBy(StringUtil.getPlatformUid());
            cmsLinkService.insert(link);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.links.link.update")
    @SLog(tag = "修改链接", msg = "链接名称:${link.name}")
    @Operation(
            tags = "CMS_链接管理", summary = "修改链接",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.link.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_link.class
    )
    public Object update(@Param("..") Cms_link link, HttpServletRequest req) {
        try {
            link.setUpdatedBy(StringUtil.getPlatformUid());
            cmsLinkService.updateIgnoreNull(link);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_链接管理", summary = "获取链接信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object get(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(cmsLinkService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("cms.links.link.delete")
    @SLog(tag = "批量删除链接")
    @Operation(
            tags = "CMS_链接管理", summary = "批量删除链接",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.link.delete")
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
                    @ApiFormParam(name = "ids", example = "a,b", description = "链接ID数组", required = true),
                    @ApiFormParam(name = "names", example = "a,b", description = "链接名称数组", required = true)
            }
    )
    public Object deleteMore(@Param("ids") String[] ids, @Param("names") String names, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            cmsLinkService.delete(ids);
            req.setAttribute("_slog_msg", names);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("cms.links.link.delete")
    @SLog(tag = "删除链接")
    @Operation(
            tags = "CMS_链接管理", summary = "删除链接",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.link.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String id, HttpServletRequest req) {
        try {
            Cms_link link = cmsLinkService.fetch(id);
            if (link == null) {
                return Result.error("system.error.noData");
            }
            cmsLinkService.delete(id);
            req.setAttribute("_slog_msg", String.format("%s", link.getName()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
