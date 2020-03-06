package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_mass;
import com.budwk.nb.wx.models.Wx_mass_news;
import com.budwk.nb.wx.models.Wx_mass_send;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxMassNewsService;
import com.budwk.nb.wx.services.WxMassSendService;
import com.budwk.nb.wx.services.WxMassService;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.weixin.bean.WxMassArticle;
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/6
 */
@IocBean
@At("/api/{version}/platform/wx/msg/mass")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_群发消息")}, servers = {@Server(url = "/")})
public class WxMsgMassController {

    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;
    @Inject
    @Reference(check = false)
    private WxMassService wxMassService;
    @Inject
    @Reference(check = false)
    private WxMassSendService wxMassSendService;
    @Inject
    @Reference(check = false)
    private WxMassNewsService wxMassNewsService;

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresAuthentication
    @Operation(
            tags = "微信_群发消息", summary = "分页查询群发信息",
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxMassService.listPageLinks(pageNo, pageSize, cnd, "massSend"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_list")
    @POST
    @Ok("json:{locked:'content'}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_群发消息", summary = "分页查询图文素材",
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object newsList(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxMassNewsService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_detail/{id}")
    @GET
    @Ok("json")
    @RequiresPermissions("wx.msg.mass")
    @Operation(
            tags = "微信_群发消息", summary = "查看图文素材内容",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "图文ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object newsDetail(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(wxMassNewsService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.news.create")
    @SLog(tag = "添加图文", msg = "图文标题:${news.title}")
    @Operation(
            tags = "微信_群发消息", summary = "添加图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.news.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_mass_news.class
    )
    public Object newsCreate(@Param("..") Wx_mass_news news, HttpServletRequest req) {
        try {
            news.setCreatedBy(StringUtil.getPlatformUid());
            news.setUpdatedBy(StringUtil.getPlatformUid());
            wxMassNewsService.insert(news);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_delete/{id}")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.news.delete")
    @SLog(tag = "删除图文")
    @Operation(
            tags = "微信_群发消息", summary = "删除图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.news.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "图文ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object deleteNews(String id, HttpServletRequest req) {
        try {
            Wx_mass_news news = wxMassNewsService.fetch(id);
            if (news == null) {
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", news.getTitle());
            wxMassNewsService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/push")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.push")
    @SLog(tag = "群发消息", msg = "群发名称:${mass.name}")
    @Operation(
            tags = "微信_群发消息", summary = "群发消息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.push")
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
                    @ApiFormParam(name = "openids", description = "openid 数组(不大于10000个)"),
                    @ApiFormParam(name = "newsids", description = "图文素材ID数组(不大于10个)")
            },
            implementation = Wx_mass.class
    )
    public Object sendDo(@Param("..") Wx_mass mass, @Param("receivers") String openids, @Param("newsids") String newsids, HttpServletRequest req) {
        try {
            WxApi2 wxApi2 = wxService.getWxApi2(mass.getWxid());
            WxOutMsg outMsg = new WxOutMsg();
            if ("news".equals(mass.getType())) {
                String[] ids = StringUtils.split(newsids, ",");
                int i = 0;
                for (String id : ids) {
                    wxMassNewsService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
                List<Wx_mass_news> newsList = wxMassNewsService.query(Cnd.where("id", "in", ids).asc("location"));
                List<WxMassArticle> articles = Json.fromJsonAsList(WxMassArticle.class, Json.toJson(newsList));
                WxResp resp = wxApi2.uploadnews(articles);
                log.debug(resp);
                String media_id = resp.media_id();
                mass.setMedia_id(media_id);
                outMsg.setMedia_id(media_id);
                outMsg.setMsgType("mpnews");
            }
            if ("text".equals(mass.getType())) {
                outMsg.setContent(mass.getContent());
                outMsg.setMsgType("text");
            }
            if ("image".equals(mass.getType())) {
                outMsg.setMedia_id(mass.getMedia_id());
                outMsg.setMsgType("image");
            }
            WxResp resp;
            if ("all".equals(mass.getScope())) {
                resp = wxApi2.mass_sendall(true, null, outMsg);
            } else {
                String[] ids = StringUtils.split(openids, ",");
                resp = wxApi2.mass_send(Arrays.asList(ids), outMsg);
            }
            log.debug(resp);
            int status = resp.errcode() == 0 ? 1 : 2;
            String errmsg = resp.getString("errmsg");
            if (status != 1) {
                return Result.error(errmsg);
            }
            mass.setStatus(resp.errcode() == 0 ? 1 : 2);
            Wx_mass wxMass = wxMassService.insert(mass);
            Wx_mass_send send = new Wx_mass_send();
            send.setWxid(wxMass.getWxid());
            send.setMassId(wxMass.getId());
            send.setErrCode(String.valueOf(resp.errcode()));
            send.setMsgId(resp.getString("msg_id"));
            if (!"all".equals(mass.getScope())) {
                send.setReceivers(openids);
            }
            send.setErrMsg(errmsg);
            send.setStatus(status);
            wxMassSendService.insert(send);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }
}
