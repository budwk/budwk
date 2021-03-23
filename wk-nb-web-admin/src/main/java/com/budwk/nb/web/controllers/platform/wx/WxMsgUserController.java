package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_config;
import com.budwk.nb.wx.models.Wx_msg_reply;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxMsgReplyService;
import com.budwk.nb.wx.services.WxMsgService;
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
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/5
 */
@IocBean
@At("/api/{version}/platform/wx/msg/user")
@Ok("json")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "微信_会员消息")}, servers = {@Server(url = "/")})
public class WxMsgUserController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;
    @Inject
    @Reference(check = false)
    private WxMsgService wxMsgService;
    @Inject
    @Reference(check = false)
    private WxMsgReplyService wxMsgReplyService;
    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresAuthentication
    @Operation(
            tags = "微信_会员消息", summary = "分页查询微信会员信息",
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
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "nickname", example = "", description = "微信昵称"),
                    @ApiFormParam(name = "content", example = "", description = "消息内容"),
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
            if (Strings.isNotBlank(openid)) {
                cnd.and("openid", "=", Strings.trim(openid));
            }
            if (Strings.isNotBlank(nickname)) {
                cnd.and("nickname", "like", "%" + Strings.trim(nickname) + "%");
            }
            if (Strings.isNotBlank(content)) {
                cnd.and("content", "like", "%" + Strings.trim(content) + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxMsgService.listPageLinks(pageNo, pageSize, cnd, "reply"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At({"/reply/{wxid}"})
    @Ok("json")
    @RequiresPermissions("wx.msg.user.reply")
    @SLog(tag = "回复微信消息", msg = "微信昵称:${nickname}")
    @Operation(
            tags = "微信_会员消息", summary = "回复微信消息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.user.reply")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH),
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
                    @ApiFormParam(name = "msgid", example = "", description = "消息ID"),
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "replyContent", example = "", description = "回复内容")
            }
    )
    public Object reply(String wxid, @Param("msgid") String msgid, @Param("nickname") String nickname, @Param("openid") String openid, @Param("replyContent") String content, HttpServletRequest req) {
        try {
            Wx_config config = wxConfigService.fetch(wxid);
            WxApi2 wxApi2 = wxService.getWxApi2(wxid);
            long now = System.currentTimeMillis() / 1000;
            WxOutMsg msg = new WxOutMsg();
            msg.setCreateTime(now);
            msg.setFromUserName(config.getAppid());
            msg.setMsgType("text");
            msg.setToUserName(openid);
            msg.setContent(content);
            WxResp wxResp = wxApi2.send(msg);
            if (wxResp.errcode() != 0) {
                return Result.error(wxResp.errmsg());
            }
            Wx_msg_reply reply = new Wx_msg_reply();
            reply.setContent(content);
            reply.setType("text");
            reply.setMsgid(msgid);
            reply.setOpenid(openid);
            reply.setWxid(wxid);
            Wx_msg_reply reply1 = wxMsgReplyService.insert(reply);
            if (reply1 != null) {
                wxMsgService.update(org.nutz.dao.Chain.make("replyId", reply1.getId()), Cnd.where("id", "=", msgid));
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
