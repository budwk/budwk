package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_tpl_id;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxTplIdService;
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
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/7
 */
@IocBean
@At("/api/{version}/platform/wx/tpl/id")
@Ok("json")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "微信_模板消息_模板编号")}, servers = {@Server(url = "/")})
public class WxTplIdController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxTplIdService wxTplIdService;
    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_模板消息_模板编号", summary = "分页查询模板编号",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("wxid") String wxid, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxTplIdService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.tpl.id.create")
    @SLog(tag = "新增模板编号", msg = "模板编号:${wxTplId.template_id}")
    @Operation(
            tags = "微信_模板消息_模板编号", summary = "新增模板编号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.tpl.id.create")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_tpl_id.class
    )
    public Object create(@Param("..") Wx_tpl_id wxTplId, HttpServletRequest req) {
        try {
            WxApi2 wxApi2 = wxService.getWxApi2(wxTplId.getWxid());
            WxResp wxResp = wxApi2.template_api_add_template(wxTplId.getId());
            if (wxResp.errcode() == 0) {
                wxTplId.setCreatedBy(StringUtil.getPlatformUid());
                wxTplId.setUpdatedBy(StringUtil.getPlatformUid());
                wxTplId.setTemplate_id(wxResp.template_id());
                wxTplIdService.insert(wxTplId);
                return Result.success();
            }
            return Result.error(wxResp.errmsg());
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("wx.tpl.id.delete")
    @SLog(tag = "批量删除模板编号")
    @Operation(
            tags = "微信_模板消息_模板编号", summary = "批量删除模板编号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.tpl.id.delete")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID", required = true),
                    @ApiFormParam(name = "ids", example = "a,b", description = "ID数组", required = true),
                    @ApiFormParam(name = "template_ids", example = "a,b", description = "模板编号数组", required = true)
            }
    )
    public Object deleteMore(@Param("wxid") String wxid, @Param("ids") String[] ids, @Param("template_ids") String template_ids, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            WxApi2 wxApi2 = wxService.getWxApi2(wxid);
            for (String id : ids) {
                Wx_tpl_id wxTplId = wxTplIdService.fetch(Cnd.where("id", "=", id).and("wxid", "=", wxid));
                WxResp wxResp = wxApi2.template_api_del_template(wxTplId.getTemplate_id());
                if (wxResp.errcode() == 0) {
                    wxTplIdService.clear(Cnd.where("id", "=", id).and("wxid", "=", wxid));
                } else {
                    return Result.error(wxResp.errmsg());
                }
            }
            wxTplIdService.delete(ids);
            req.setAttribute("_slog_msg", String.format("模板编号:%s", template_ids));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete")
    @Ok("json")
    @POST
    @RequiresPermissions("wx.tpl.id.delete")
    @SLog(tag = "删除模板编号")
    @Operation(
            tags = "微信_模板消息_模板编号", summary = "删除模板编号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.tpl.id.delete")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID", required = true),
                    @ApiFormParam(name = "id", example = "", description = "模板库ID", required = true)
            }
    )
    public Object delete(@Param("wxid") String wxid, @Param("id") String id, HttpServletRequest req) {
        try {
            Wx_tpl_id wxTplId = wxTplIdService.fetch(Cnd.where("id", "=", id).and("wxid", "=", wxid));
            if (wxTplId == null) {
                return Result.error("system.error.noData");
            }
            WxApi2 wxApi2 = wxService.getWxApi2(wxid);
            WxResp wxResp = wxApi2.template_api_del_template(wxTplId.getTemplate_id());
            if (wxResp.errcode() == 0) {
                wxTplIdService.clear(Cnd.where("id", "=", id).and("wxid", "=", wxTplId.getWxid()));
                req.setAttribute("_slog_msg", String.format("模板编号:%s", wxTplId.getTemplate_id()));
                return Result.success();
            } else {
                return Result.error(wxResp.errmsg());
            }
        } catch (Exception e) {
            return Result.error();
        }
    }
}
