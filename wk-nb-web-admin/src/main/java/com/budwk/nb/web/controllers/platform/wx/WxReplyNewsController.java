package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.wx.models.Wx_reply_news;
import com.budwk.nb.wx.services.WxReplyNewsService;
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
 * @date 2020/3/5
 */
@IocBean
@At("/api/{version}/platform/wx/reply/news")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_自动回复_图文管理")}, servers = {@Server(url = "/")})
public class WxReplyNewsController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxReplyNewsService wxReplyNewsService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_自动回复_图文管理", summary = "分页查询图文",
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
            return Result.success().addData(wxReplyNewsService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.reply.news.create")
    @SLog(tag = "添加图文", msg = "图文链接:${replyNews.title}")
    @Operation(
            tags = "微信_自动回复_图文管理", summary = "添加图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.news.create")
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
            implementation = Wx_reply_news.class
    )
    public Object create(@Param("..") Wx_reply_news replyNews, HttpServletRequest req) {
        try {
            replyNews.setCreatedBy(StringUtil.getPlatformUid());
            replyNews.setUpdatedBy(StringUtil.getPlatformUid());
            wxReplyNewsService.insert(replyNews);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.reply.news.update")
    @SLog(tag = "修改图文", msg = "图文链接:${replyNews.title}")
    @Operation(
            tags = "微信_自动回复_图文管理", summary = "修改图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.news.update")
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
            implementation = Wx_reply_news.class
    )
    public Object update(@Param("..") Wx_reply_news replyNews, HttpServletRequest req) {
        try {
            replyNews.setUpdatedBy(StringUtil.getPlatformUid());
            wxReplyNewsService.updateIgnoreNull(replyNews);
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
            tags = "微信_自动回复_图文管理", summary = "获取图文信息",
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
            return Result.success().addData(wxReplyNewsService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("wx.reply.news.delete")
    @SLog(tag = "批量删除图文")
    @Operation(
            tags = "微信_自动回复_图文管理", summary = "批量删除图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.news.delete")
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
                    @ApiFormParam(name = "ids", example = "a,b", description = "文本ID数组", required = true),
                    @ApiFormParam(name = "titles", example = "a,b", description = "图文标题数组", required = true)
            }
    )
    public Object deleteMore(@Param("ids") String[] ids, @Param("titles") String titles, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            wxReplyNewsService.delete(ids);
            req.setAttribute("_slog_msg", String.format("图文链接:%s", titles));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.reply.news.delete")
    @SLog(tag = "删除图文")
    @Operation(
            tags = "微信_自动回复_图文管理", summary = "删除图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.reply.news.delete")
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
            Wx_reply_news replyNews = wxReplyNewsService.fetch(id);
            if (replyNews == null) {
                return Result.error("system.error.noData");
            }
            wxReplyNewsService.delete(id);
            req.setAttribute("_slog_msg", String.format("图文链接:%s", replyNews.getTitle()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

}
