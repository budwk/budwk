package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_mass;
import com.budwk.app.wx.models.Wx_mass_news;
import com.budwk.app.wx.models.Wx_mass_send;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxMassNewsService;
import com.budwk.app.wx.services.WxMassSendService;
import com.budwk.app.wx.services.WxMassService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
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
@At("/wechat/admin/msg/mass")
@Ok("json")
@ApiDefinition(tag = "群发消息")
@SLog(tag = "群发消息")
@Slf4j
public class WxMsgMassController {

    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;
    @Inject
    private WxMassService wxMassService;
    @Inject
    private WxMassSendService wxMassSendService;
    @Inject
    private WxMassNewsService wxMassNewsService;

    @At("/list")
    @POST
    @Ok("json:full")
    @SaCheckLogin
    @ApiOperation(description = "分页查询群发信息")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
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
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxMassService.listPageLinks(pageNo, pageSize, cnd, "massSend"));
    }

    @At("/news_list")
    @POST
    @Ok("json:{locked:'content'}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询图文素材")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> newsList(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxMassNewsService.listPage(pageNo, pageSize, cnd));
    }

    @At("/news_detail/{id}")
    @GET
    @Ok("json")
    @SaCheckPermission("wx.msg.mass")
    @ApiOperation(description = "查看图文素材内容")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> newsDetail(String id, HttpServletRequest req) {
        return Result.data(wxMassNewsService.fetch(id));
    }

    @At("/news_create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.msg.mass.news.create")
    @SLog("图文标题:${news.title}")
    @ApiOperation(description = "添加图文")
    @ApiFormParams(
            implementation = Wx_mass_news.class
    )
    @ApiResponses
    public Result<?> newsCreate(@Param("..") Wx_mass_news news, HttpServletRequest req) {
        news.setCreatedBy(SecurityUtil.getUserId());
        wxMassNewsService.insert(news);
        return Result.success();
    }

    @At("/news_delete/{id}")
    @Ok("json")
    @SaCheckPermission("wx.msg.mass.news.delete")
    @SLog(tag = "删除图文")
    @ApiOperation(description = "删除图文")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> deleteNews(String id, HttpServletRequest req) {
        Wx_mass_news news = wxMassNewsService.fetch(id);
        if (news == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        req.setAttribute("_slog_msg", news.getTitle());
        wxMassNewsService.delete(id);
        return Result.success();
    }

    @At("/push")
    @Ok("json")
    @SaCheckPermission("wx.msg.mass.push")
    @SLog("群发名称:${mass.name}")
    @ApiOperation(description = "群发消息")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "openids", description = "openid 数组(不大于10000个)"),
                    @ApiFormParam(name = "newsids", description = "图文素材ID数组(不大于10个)")
            },
            implementation = Wx_mass.class
    )
    @ApiResponses
    public Result<?> sendDo(@Param("..") Wx_mass mass, @Param("receivers") String openids, @Param("newsids") String newsids, HttpServletRequest req) {
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
        WxResp resp = null;
        if ("all".equals(mass.getScope())) {
            resp = wxApi2.mass_sendall(true, null, outMsg);
        } else {
            String[] ids = StringUtils.split(openids, ",");
            resp = wxApi2.mass_send(Arrays.asList(ids), outMsg);
        }
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
    }
}
