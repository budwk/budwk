package com.budwk.nb.web.controllers.platform.cms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.cms.models.Cms_link_class;
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
@At("/api/{version}/platform/cms/link/class")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "CMS_链接分类")}, servers = {@Server(url = "/")})
public class CmsLinkClassController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private CmsLinkClassService cmsLinkClassService;

    @Inject
    @Reference(check = false)
    private CmsLinkService cmsLinkService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("cms.links.class")
    @Operation(
            tags = "CMS_链接分类", summary = "分页查询链接分类",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.class")
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(cmsLinkClassService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.links.class.create")
    @SLog(tag = "新增链接分类", msg = "分类名称:${linkClass.name}")
    @Operation(
            tags = "CMS_链接分类", summary = "新增链接分类",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.class.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_link_class.class
    )
    public Object create(@Param("..") Cms_link_class linkClass, HttpServletRequest req) {
        try {
            int codeCount = cmsLinkClassService.count(Cnd.where("code", "=", Strings.sNull(linkClass.getCode())));
            if (codeCount > 0) {
                return Result.error("cms.links.class.form.code.exist");
            }
            linkClass.setCreatedBy(StringUtil.getPlatformUid());
            linkClass.setUpdatedBy(StringUtil.getPlatformUid());
            cmsLinkClassService.insert(linkClass);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.links.class.update")
    @SLog(tag = "修改链接分类", msg = "分类名称:${linkClass.name}")
    @Operation(
            tags = "CMS_链接分类", summary = "修改链接分类",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.class.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_link_class.class
    )
    public Object update(@Param("..") Cms_link_class linkClass, HttpServletRequest req) {
        try {
            Cms_link_class dbClass = cmsLinkClassService.fetch(linkClass.getId());
            if (!Strings.sNull(linkClass.getCode()).equals(dbClass.getCode())) {
                int codeCount = cmsLinkClassService.count(Cnd.where("code", "=", Strings.sNull(dbClass.getCode())));
                if (codeCount > 0) {
                    return Result.error("cms.links.class.form.code.exist");
                }
            }
            linkClass.setUpdatedBy(StringUtil.getPlatformUid());
            cmsLinkClassService.updateIgnoreNull(linkClass);
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
            tags = "CMS_链接分类", summary = "获取链接分类信息",
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
            return Result.success().addData(cmsLinkClassService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("cms.links.class.delete")
    @SLog(tag = "删除链接分类")
    @Operation(
            tags = "CMS_链接分类", summary = "删除链接分类",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.links.class.delete")
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
            Cms_link_class linkClass = cmsLinkClassService.fetch(id);
            if (linkClass == null) {
                return Result.error("system.error.noData");
            }
            cmsLinkClassService.delete(id);
            cmsLinkService.clear(Cnd.where("classId", "=", id));
            req.setAttribute("_slog_msg", String.format("%s", linkClass.getName()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
