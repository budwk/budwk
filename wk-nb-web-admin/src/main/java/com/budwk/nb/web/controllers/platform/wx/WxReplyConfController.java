package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.wx.models.Wx_reply;
import com.budwk.nb.wx.services.*;
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
 * @date 2020/3/7
 */
@IocBean
@At("/api/{version}/platform/wx/reply/conf")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_自动回复_事件配置")}, servers = {@Server(url = "/")})
public class WxReplyConfController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxReplyTxtService wxReplyTxtService;
    @Inject
    @Reference(check = false)
    private WxReplyNewsService wxReplyNewsService;
    @Inject
    @Reference(check = false)
    private WxReplyImgService wxReplyImgService;
    @Inject
    @Reference(check = false)
    private WxReplyService wxReplyService;
    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;


    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_自动回复_事件配置", summary = "分页查询事件",
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
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "type", example = "", description = "事件类型"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("wxid") String wxid, @Param("type") String type, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(type)) {
                cnd.and("type", "=", type);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxReplyService.listPageLinks(pageNo, pageSize, cnd, "^(replyImg|replyTxt|replyNews)$"));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.reply.conf.create")
    @SLog(tag = "新增事件", msg = "事件类型:${reply.type}")
    @Operation(
            tags = "微信_自动回复_事件配置", summary = "新增事件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.conf.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_reply.class
    )
    public Object create(@Param("..") Wx_reply reply, HttpServletRequest req) {
        try {
            if ("follow".equals(reply.getType())) {
                int c = wxReplyService.count(Cnd.where("type", "=", "follow").and("wxid", "=", reply.getWxid()));
                if (c > 0) {
                    return Result.error("wx.reply.conf.error.follow.only");
                }
            }
            if ("keyword".equals(reply.getType())) {
                int c = wxReplyService.count(Cnd.where("keyword", "=", reply.getKeyword()).and("wxid", "=", reply.getWxid()));
                if (c > 0) {
                    return Result.error("wx.reply.conf.error.keyword.exist");
                }
            }
            reply.setCreatedBy(StringUtil.getPlatformUid());
            reply.setUpdatedBy(StringUtil.getPlatformUid());
            wxReplyService.insert(reply);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.reply.conf.update")
    @SLog(tag = "修改事件", msg = "事件类型:${reply.type}")
    @Operation(
            tags = "微信_自动回复_事件配置", summary = "修改事件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.conf.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_reply.class
    )
    public Object update(@Param("..") Wx_reply reply, HttpServletRequest req) {
        try {
            reply.setUpdatedBy(StringUtil.getPlatformUid());
            wxReplyService.updateIgnoreNull(reply);
            if ("news".equals(reply.getMsgType())) {
                String[] newsIds = Strings.sBlank(reply.getContent()).split(",");
                int i = 0;
                for (String id : newsIds) {
                    wxReplyNewsService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
            }
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
            tags = "微信_自动回复_事件配置", summary = "获取事件内容",
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
            Wx_reply reply = wxReplyService.fetch(id);
            if ("txt".equals(reply.getMsgType())) {
                req.setAttribute("txt", wxReplyTxtService.fetch(reply.getContent()));
            } else if ("news".equals(reply.getMsgType())) {
                req.setAttribute("txt", wxReplyTxtService.fetch(reply.getContent()));
            } else if ("image".equals(reply.getMsgType())) {
                req.setAttribute("image", wxReplyImgService.fetch(reply.getContent()));
            }
            return Result.success().addData(reply);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("wx.reply.conf.delete")
    @SLog(tag = "批量删除事件")
    @Operation(
            tags = "微信_自动回复_事件配置", summary = "批量删除事件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.conf.delete")
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
                    @ApiFormParam(name = "ids", example = "a,b", description = "事件ID数组", required = true),
                    @ApiFormParam(name = "types", example = "a,b", description = "事件类型数组", required = true)
            }
    )
    public Object deleteMore(@Param("ids") String[] ids, @Param("types") String types, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            wxReplyService.delete(ids);
            req.setAttribute("_slog_msg", String.format("事件类型:%s", types));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.reply.conf.delete")
    @SLog(tag = "删除事件")
    @Operation(
            tags = "微信_自动回复_事件配置", summary = "删除事件",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.conf.delete")
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
            Wx_reply replyt = wxReplyService.fetch(id);
            if (replyt == null) {
                return Result.error("system.error.noData");
            }
            wxReplyService.delete(id);
            req.setAttribute("_slog_msg", String.format("事件类型:%s", replyt.getType()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
