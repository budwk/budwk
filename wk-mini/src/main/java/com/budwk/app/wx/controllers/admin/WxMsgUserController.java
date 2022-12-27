package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.models.Wx_msg_reply;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxMsgReplyService;
import com.budwk.app.wx.services.WxMsgService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/5
 */
@IocBean
@At("/wechat/admin/msg/user")
@Ok("json")
@ApiDefinition(tag = "会员消息")
@SLog(tag = "会员消息")
@Slf4j
public class WxMsgUserController {
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxMsgService wxMsgService;
    @Inject
    private WxMsgReplyService wxMsgReplyService;
    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:full")
    @SaCheckLogin
    @ApiOperation(description = "分页查询微信会员信息")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "nickname", example = "", description = "微信昵称"),
                    @ApiFormParam(name = "content", example = "", description = "消息内容"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
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
        return Result.data(wxMsgService.listPageLinks(pageNo, pageSize, cnd, "reply"));
    }

    @At({"/reply/{wxid}"})
    @Ok("json")
    @SaCheckPermission("wx.msg.user.reply")
    @SLog("微信昵称:${nickname}")
    @ApiOperation(description = "回复微信消息")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "msgid", example = "", description = "消息ID"),
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "replyContent", example = "", description = "回复内容")
            }
    )
    @ApiResponses
    public Result<?> reply(String wxid, @Param("msgid") String msgid, @Param("nickname") String nickname, @Param("openid") String openid, @Param("replyContent") String content, HttpServletRequest req) {
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
    }
}
