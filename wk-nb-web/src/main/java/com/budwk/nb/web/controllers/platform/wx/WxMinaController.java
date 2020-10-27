package com.budwk.nb.web.controllers.platform.wx;

import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.wx.models.Wx_mina;
import com.budwk.nb.wx.services.WxMinaService;
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
import java.util.List;

@IocBean
@At("/api/{version}/platform/wx/conf/mina")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_小程序配置")}, servers = {@Server(url = "/")})
public class WxMinaController {
    private static final Log log = Logs.get();

    @Inject
    private WxMinaService wxMinaService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_小程序配置", summary = "分页查询",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER)
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
            Pagination pagination = wxMinaService.listPage(pageNo, pageSize, cnd);
            List<Wx_mina> list = pagination.getList(Wx_mina.class);
            list.forEach(
                    wxMina -> {
                        String appSecret = Strings.sNull(wxMina.getAppsecret());
                        if (appSecret.length() > 10) {
                            wxMina.setAppsecret(appSecret.substring(0, 10) + "*****");
                        }
                    }
            );
            pagination.setList(list);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.mina.create")
    @SLog(tag = "新增小程序", msg = "小程序名称:${wxMina.appname}")
    @Operation(
            tags = "微信_小程序配置", summary = "新增小程序",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.mina.create")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_mina.class
    )
    public Object create(@Param("..") Wx_mina wxMina, HttpServletRequest req) {
        try {
            wxMina.setCreatedBy(StringUtil.getPlatformUid());
            wxMina.setUpdatedBy(StringUtil.getPlatformUid());
            wxMinaService.insert(wxMina);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.mina.update")
    @SLog(tag = "修改小程序", msg = "小程序名称:${wxMina.appname}")
    @Operation(
            tags = "微信_小程序配置", summary = "修改小程序",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.mina.update")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_mina.class
    )
    public Object update(@Param("..") Wx_mina wxMina, HttpServletRequest req) {
        try {
            wxMina.setUpdatedBy(StringUtil.getPlatformUid());
            String appSecret = Strings.sNull(wxMina.getAppsecret());
            if (appSecret.contains("***")) {// 包含**不入库
                wxMina.setAppsecret(null);
            }
            wxMinaService.updateIgnoreNull(wxMina);
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
            tags = "微信_小程序配置", summary = "获取小程序",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER)
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
            Wx_mina wxMina = wxMinaService.fetch(id);
            String appSecret = Strings.sNull(wxMina.getAppsecret());
            if (appSecret.length() > 10) {
                wxMina.setAppsecret(appSecret.substring(0, 10) + "*****");
            }
            return Result.success().addData(wxMina);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.conf.mina.delete")
    @SLog(tag = "删除小程序")
    @Operation(
            tags = "微信_小程序配置", summary = "删除小程序",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.mina.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER)
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
            Wx_mina wxMina = wxMinaService.fetch(id);
            if (wxMina == null) {
                return Result.error("system.error.noData");
            }
            wxMinaService.delete(id);
            req.setAttribute("_slog_msg", String.format("删除小程序:%s", wxMina.getAppname()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
