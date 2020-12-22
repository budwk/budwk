package com.budwk.nb.web.controllers.platform.cms;

import com.budwk.nb.cms.models.Cms_site;
import com.budwk.nb.cms.services.CmsSiteService;
import com.budwk.nb.base.annotation.SLog;
import com.budwk.nb.base.result.Result;
import com.budwk.nb.base.page.Pagination;
import com.budwk.nb.base.utils.PageUtil;
import com.budwk.nb.base.utils.StringUtil;
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
 * @date 2020/2/10
 */
@IocBean
@At("/api/{version}/platform/cms/site")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "CMS_站点管理")}, servers = {@Server(url = "/")})
public class CmsSiteController {
    private static final Log log = Logs.get();
    @Inject
    private CmsSiteService cmsSiteService;

    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("cms.sites.site")
    @Operation(
            tags = "CMS_站点管理", summary = "分页查询站点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.sites.site")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
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
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = cmsSiteService.listPage(pageNo, pageSize, cnd);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At
    @POST
    @Ok("json")
    @RequiresPermissions("cms.sites.site.create")
    @SLog(tag = "新建站点", msg = "站点名称:${name}")
    @Operation(
            tags = "CMS_站点管理", summary = "新建站点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.sites.site.create")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_site.class
    )
    public Object create(@Param("..") Cms_site site, HttpServletRequest req) {
        try {
            site.setCreatedBy(StringUtil.getPlatformUid());
            site.setUpdatedBy(StringUtil.getPlatformUid());
            cmsSiteService.insert(site);
            cmsSiteService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At
    @POST
    @Ok("json")
    @RequiresPermissions("cms.sites.site.update")
    @SLog(tag = "修改站点", msg = "站点名称:${name}")
    @Operation(
            tags = "CMS_站点管理", summary = "修改站点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.sites.site.update")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_site.class
    )
    public Object update(@Param("..") Cms_site site, HttpServletRequest req) {
        try {
            site.setUpdatedBy(StringUtil.getPlatformUid());
            cmsSiteService.updateIgnoreNull(site);
            cmsSiteService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/delete/{id}")
    @DELETE
    @Ok("json")
    @RequiresPermissions("cms.sites.site.delete")
    @SLog(tag = "删除站点", msg = "站点名称:${name}")
    @Operation(
            tags = "CMS_站点管理", summary = "删除站点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.sites.site.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
            cmsSiteService.delete(id);
            cmsSiteService.clearCache();
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
            tags = "CMS_站点管理", summary = "获取站点信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
            return Result.success().addData(cmsSiteService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }
}
